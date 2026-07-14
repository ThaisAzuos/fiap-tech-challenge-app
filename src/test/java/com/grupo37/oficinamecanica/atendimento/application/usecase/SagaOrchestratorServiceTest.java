package com.grupo37.oficinamecanica.atendimento.application.usecase;

import com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.OrdemServicoEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Testes unitários do orquestrador da Saga (Fase 4 / Dia 3). Verifica que
 * cada evento recebido é traduzido na chamada correta a AtendimentoService
 * (estratégia aditiva/não invasiva registrada na ADR-009) e que a
 * compensação publica SagaCompensada com a etapa e o motivo corretos.
 */
@ExtendWith(MockitoExtension.class)
class SagaOrchestratorServiceTest {

    @Mock
    private AtendimentoService atendimentoService;

    @Mock
    private OrdemServicoEventPublisher eventPublisher;

    private SagaOrchestratorService sagaOrchestratorService;

    @BeforeEach
    void setUp() {
        sagaOrchestratorService = new SagaOrchestratorService(atendimentoService, eventPublisher);
    }

    @Test
    void deveAprovarOrcamentoQuandoOrcamentoAprovado() {
        UUID osId = UUID.randomUUID();

        sagaOrchestratorService.tratarOrcamentoAprovado(osId);

        verify(atendimentoService).aprovarOrcamento(osId);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveConcluirOrdemServicoQuandoExecucaoConcluida() {
        UUID osId = UUID.randomUUID();

        sagaOrchestratorService.tratarExecucaoConcluida(osId);

        verify(atendimentoService).concluirOrdemServico(osId);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void naoDeveAlterarStatusQuandoPagamentoConfirmado() {
        UUID osId = UUID.randomUUID();

        sagaOrchestratorService.tratarPagamentoConfirmado(osId);

        verify(atendimentoService, never()).aprovarOrcamento(any());
        verify(atendimentoService, never()).concluirOrdemServico(any());
        verify(atendimentoService, never()).cancelarOrdemServico(any(), any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveCancelarOrdemServicoECompensarQuandoOrcamentoReprovado() {
        UUID osId = UUID.randomUUID();
        String motivo = "Cliente não aprovou o valor";

        sagaOrchestratorService.tratarOrcamentoReprovado(osId, motivo);

        verify(atendimentoService).cancelarOrdemServico(osId, motivo);
        verify(eventPublisher).publicarSagaCompensada(eq(osId), eq("ORCAMENTO"), eq(motivo), anyList());
    }

    @Test
    void deveCancelarOrdemServicoECompensarQuandoPagamentoFalhou() {
        UUID osId = UUID.randomUUID();
        String motivo = "Pagamento recusado pela operadora";

        sagaOrchestratorService.tratarPagamentoFalhou(osId, motivo);

        verify(atendimentoService).cancelarOrdemServico(osId, motivo);
        verify(eventPublisher).publicarSagaCompensada(eq(osId), eq("PAGAMENTO"), eq(motivo), anyList());
    }

    @Test
    void deveUsarMotivoPadraoQuandoCompensacaoSemMotivoInformado() {
        UUID osId = UUID.randomUUID();

        sagaOrchestratorService.tratarOrcamentoReprovado(osId, null);

        verify(atendimentoService).cancelarOrdemServico(eq(osId), eq("Saga compensada na etapa ORCAMENTO"));
        verify(eventPublisher).publicarSagaCompensada(eq(osId), eq("ORCAMENTO"), eq("Saga compensada na etapa ORCAMENTO"), anyList());
    }

    @Test
    void acoesCompensatoriasPublicadasNaoDevemSerVazias() {
        UUID osId = UUID.randomUUID();

        sagaOrchestratorService.tratarPagamentoFalhou(osId, "Pagamento recusado");

        verify(eventPublisher).publicarSagaCompensada(eq(osId), eq("PAGAMENTO"), any(),
                org.mockito.ArgumentMatchers.argThat((List<String> acoes) -> acoes != null && !acoes.isEmpty()));
    }
}
