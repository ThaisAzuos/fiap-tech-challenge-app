package com.grupo37.oficinamecanica.bdd;

import com.grupo37.oficinamecanica.atendimento.application.usecase.AtendimentoService;
import com.grupo37.oficinamecanica.atendimento.application.usecase.SagaOrchestratorService;
import com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.OrdemServicoEventPublisher;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Passos do cenário de BDD da Saga (Dia 6 - Fase 4). Exercita o
 * SagaOrchestratorService (o orquestrador real, sem mocks nele mesmo) com
 * AtendimentoService e OrdemServicoEventPublisher mockados — o mesmo
 * limite de teste usado em SagaOrchestratorServiceTest, aqui narrado como
 * um fluxo de negócio de ponta a ponta (fluxo feliz + 2 cenários de
 * compensação/rollback), conforme pedido no Dia 6 do plano de execução.
 *
 * Este teste não sobe um contexto Spring nem depende de RabbitMQ/Postgres —
 * ver ADR-010 para a justificativa dessa escolha de escopo.
 */
public class SagaSteps {

    private AtendimentoService atendimentoService;
    private OrdemServicoEventPublisher eventPublisher;
    private SagaOrchestratorService sagaOrchestratorService;
    private UUID ordemServicoId;

    @Before
    public void setUp() {
        atendimentoService = mock(AtendimentoService.class);
        eventPublisher = mock(OrdemServicoEventPublisher.class);
        sagaOrchestratorService = new SagaOrchestratorService(atendimentoService, eventPublisher);
    }

    @Given("uma Ordem de Serviço {string}")
    public void umaOrdemDeServico(String nomeAmigavel) {
        // UUID determinístico a partir do nome amigável do cenário, para manter os passos legíveis.
        ordemServicoId = UUID.nameUUIDFromBytes(nomeAmigavel.getBytes());
    }

    @When("o orçamento da Ordem de Serviço é aprovado")
    public void oOrcamentoDaOrdemDeServicoEAprovado() {
        sagaOrchestratorService.tratarOrcamentoAprovado(ordemServicoId);
    }

    @When("o orçamento da Ordem de Serviço é reprovado com o motivo {string}")
    public void oOrcamentoDaOrdemDeServicoEReprovadoComOMotivo(String motivo) {
        sagaOrchestratorService.tratarOrcamentoReprovado(ordemServicoId, motivo);
    }

    @When("o pagamento da Ordem de Serviço é confirmado")
    public void oPagamentoDaOrdemDeServicoEConfirmado() {
        sagaOrchestratorService.tratarPagamentoConfirmado(ordemServicoId);
    }

    @When("o pagamento da Ordem de Serviço falha com o motivo {string}")
    public void oPagamentoDaOrdemDeServicoFalhaComOMotivo(String motivo) {
        sagaOrchestratorService.tratarPagamentoFalhou(ordemServicoId, motivo);
    }

    @When("a execução da Ordem de Serviço é concluída")
    public void aExecucaoDaOrdemDeServicoEConcluida() {
        sagaOrchestratorService.tratarExecucaoConcluida(ordemServicoId);
    }

    @Then("a Ordem de Serviço é aprovada e avança para execução")
    public void aOrdemDeServicoEAprovadaEAvancaParaExecucao() {
        verify(atendimentoService).aprovarOrcamento(ordemServicoId);
    }

    @Then("nenhuma transição adicional de status é realizada")
    public void nenhumaTransicaoAdicionalDeStatusERealizada() {
        verify(atendimentoService, never()).aprovarOrcamento(any());
        verify(atendimentoService, never()).cancelarOrdemServico(any(), any());
        verify(atendimentoService, never()).concluirOrdemServico(any());
    }

    @Then("a Ordem de Serviço é finalizada")
    public void aOrdemDeServicoEFinalizada() {
        verify(atendimentoService).concluirOrdemServico(ordemServicoId);
    }

    @Then("a Ordem de Serviço é cancelada com o motivo {string}")
    public void aOrdemDeServicoECanceladaComOMotivo(String motivo) {
        verify(atendimentoService).cancelarOrdemServico(ordemServicoId, motivo);
    }

    @Then("nenhum evento de compensação é publicado")
    public void nenhumEventoDeCompensacaoEPublicado() {
        verify(eventPublisher, never()).publicarSagaCompensada(any(), any(), any(), anyList());
    }

    @Then("um evento de compensação da Saga é publicado para a etapa {string}")
    public void umEventoDeCompensacaoDaSagaEPublicadoParaAEtapa(String etapa) {
        verify(eventPublisher).publicarSagaCompensada(eq(ordemServicoId), eq(etapa), any(), anyList());
    }
}
