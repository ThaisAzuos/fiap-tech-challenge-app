package com.grupo37.oficinamecanica.atendimento.application.usecase;

import com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.OrdemServicoEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Orquestrador da Saga no OS Service, reagindo aos eventos publicados pelo
 * Billing Service e pelo Execution Service.
 *
 * Estratégia adotada (Dia 3, "strangler fig" / não invasiva — ver ADR-009):
 * em vez de reescrever a lógica de domínio da OS, este orquestrador REUTILIZA
 * os métodos públicos já existentes e testados de AtendimentoService
 * (aprovarOrcamento, cancelarOrdemServico, concluirOrdemServico), que por sua
 * vez já validam as transições de status via OrdemServico.atualizarStatus().
 * Isso mantém o domínio existente intocado e reduz o risco de regressão.
 *
 * Mapeamento evento -> status da OS (simplificação registrada na ADR-009,
 * dado que StatusOS não tem um estado dedicado para "orçamento aprovado,
 * aguardando pagamento"):
 *   OrcamentoAprovado   -> AGUARDANDO_APROVACAO -> EM_EXECUCAO (aprovarOrcamento)
 *   PagamentoConfirmado -> apenas log/auditoria (sem transição adicional de status)
 *   ExecucaoConcluida   -> EM_EXECUCAO -> FINALIZADA (concluirOrdemServico)
 *   OrcamentoReprovado  -> compensação: cancela a OS (cancelarOrdemServico)
 *   PagamentoFalhou     -> compensação: cancela a OS (cancelarOrdemServico)
 */
@Slf4j
@Service
public class SagaOrchestratorService {

    private final AtendimentoService atendimentoService;
    private final OrdemServicoEventPublisher eventPublisher;

    public SagaOrchestratorService(AtendimentoService atendimentoService, OrdemServicoEventPublisher eventPublisher) {
        this.atendimentoService = atendimentoService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void tratarOrcamentoAprovado(UUID ordemServicoId) {
        atendimentoService.aprovarOrcamento(ordemServicoId);
        log.info("[Saga] OS {} avançou para EM_EXECUCAO após OrcamentoAprovado", ordemServicoId);
    }

    @Transactional
    public void tratarOrcamentoReprovado(UUID ordemServicoId, String motivo) {
        compensar(ordemServicoId, "ORCAMENTO", motivo);
    }

    @Transactional
    public void tratarPagamentoConfirmado(UUID ordemServicoId) {
        // Sem transição adicional de status nesta fase (ver mapeamento acima / ADR-009).
        log.info("[Saga] PagamentoConfirmado recebido para a OS {} (registro para auditoria)", ordemServicoId);
    }

    @Transactional
    public void tratarPagamentoFalhou(UUID ordemServicoId, String motivo) {
        compensar(ordemServicoId, "PAGAMENTO", motivo);
    }

    @Transactional
    public void tratarExecucaoConcluida(UUID ordemServicoId) {
        atendimentoService.concluirOrdemServico(ordemServicoId);
        log.info("[Saga] OS {} avançou para FINALIZADA após ExecucaoConcluida", ordemServicoId);
    }

    private void compensar(UUID ordemServicoId, String etapaFalha, String motivo) {
        String motivoFinal = (motivo == null || motivo.isBlank())
                ? "Saga compensada na etapa " + etapaFalha
                : motivo;

        atendimentoService.cancelarOrdemServico(ordemServicoId, motivoFinal);
        log.warn("[Saga] OS {} CANCELADA por compensação na etapa {}: {}", ordemServicoId, etapaFalha, motivoFinal);

        eventPublisher.publicarSagaCompensada(
                ordemServicoId,
                etapaFalha,
                motivoFinal,
                List.of("Cancelar Ordem de Serviço", "Liberar posição na fila de execução (se aplicável)")
        );
    }
}
