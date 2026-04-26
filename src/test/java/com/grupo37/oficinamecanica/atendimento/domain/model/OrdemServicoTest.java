package com.grupo37.oficinamecanica.atendimento.domain.model;

import com.grupo37.oficinamecanica.cadastro.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.model.Veiculo;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import com.grupo37.oficinamecanica.estoque.model.Peca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrdemServicoTest {

    private Veiculo veiculo;
    private Cliente cliente;
    private Peca peca1;
    private Peca peca2;

    @BeforeEach
    void setUp() {
        // Criação de mocks das dependências
        cliente = mock(Cliente.class);
        when(cliente.getNome()).thenReturn("João Silva");

        veiculo = mock(Veiculo.class);
        when(veiculo.getPlaca()).thenReturn("ABC1234");
        when(veiculo.getModelo()).thenReturn("Fiat Uno");
        when(veiculo.getDono()).thenReturn(cliente);

        peca1 = mock(Peca.class);
        when(peca1.getId()).thenReturn(UUID.randomUUID());
        when(peca1.getNome()).thenReturn("Pastilha de Freio");
        when(peca1.getPreco()).thenReturn(BigDecimal.valueOf(150.00));

        peca2 = mock(Peca.class);
        when(peca2.getId()).thenReturn(UUID.randomUUID());
        when(peca2.getNome()).thenReturn("Óleo de Motor");
        when(peca2.getPreco()).thenReturn(BigDecimal.valueOf(80.00));
    }

    @Test
    void deveCriarOrdemServicoComStatusRecebida() {
        // Dado
        String descricaoProblema = "Problema no freio";

        // Quando
        OrdemServico os = new OrdemServico(veiculo, descricaoProblema);

        // Então
        assertThat(os.getVeiculo()).isEqualTo(veiculo);
        assertThat(os.getDescricaoProblema()).isEqualTo(descricaoProblema);
        assertThat(os.getStatus()).isEqualTo(StatusOS.RECEBIDA);
        assertThat(os.getDataAbertura()).isNotNull();
        assertThat(os.getValorTotal()).isEqualTo(BigDecimal.ZERO);
        assertThat(os.getItens()).isEmpty();
    }

    @Test
    void deveAdicionarPecaNaOrdemServico() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema no freio");
        int quantidade = 2;

        // Quando
        os.adicionarPeca(peca1, quantidade);

        // Então
        assertThat(os.getItens()).hasSize(1);
        var item = os.getItens().get(0);
        assertThat(item.getPecaId()).isEqualTo(peca1.getId());
        assertThat(item.getNomePecaSnapshot()).isEqualTo(peca1.getNome());
        assertThat(item.getQuantidade()).isEqualTo(quantidade);
        assertThat(item.getPrecoNoMomento()).isEqualTo(peca1.getPreco());
        assertThat(os.getValorTotal()).isEqualTo(BigDecimal.valueOf(300.00)); // 150 * 2
    }

    @Test
    void deveAtualizarValorTotalAoAdicionarMultiplasPecas() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema geral");

        // Quando
        os.adicionarPeca(peca1, 1); // 150.00
        os.adicionarPeca(peca2, 2); // 160.00

        // Então
        assertThat(os.getValorTotal()).isEqualTo(BigDecimal.valueOf(310.00));
        assertThat(os.getItens()).hasSize(2);
    }

    @Test
    void naoDevePermitirAdicionarPecaEmOrdemServicoFinalizada() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema");
        // Percorre o fluxo correto até atingir FINALIZADA
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        os.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        os.atualizarStatus(StatusOS.FINALIZADA);

        // Quando/Então
        assertThatThrownBy(() -> os.adicionarPeca(peca1, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível alterar uma OS já encerrada (FINALIZADA).");
    }

    @Test
    void naoDevePermitirAdicionarPecaEmOrdemServicoEntregue() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema");
        // Percorre o fluxo correto até atingir ENTREGUE
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        os.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        os.atualizarStatus(StatusOS.FINALIZADA);
        os.atualizarStatus(StatusOS.ENTREGUE);

        // Quando/Então
        assertThatThrownBy(() -> os.adicionarPeca(peca1, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível alterar uma OS já encerrada (ENTREGUE).");
    }

    @Test
    void devePermitirTransicaoDeStatusValida() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema");

        // Quando
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // Então
        assertThat(os.getStatus()).isEqualTo(StatusOS.EM_DIAGNOSTICO);
    }

    @Test
    void naoDevePermitirTransicaoDiretaDeRecebidaParaFinalizada() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema");

        // Quando/Então
        assertThatThrownBy(() -> os.atualizarStatus(StatusOS.FINALIZADA))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A OS precisa passar por diagnóstico antes de ser finalizada ou entregue.");
    }

    @Test
    void naoDevePermitirRetrocessoDeStatus() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema");
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // Quando/Então
        assertThatThrownBy(() -> os.atualizarStatus(StatusOS.RECEBIDA))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é permitido retornar a Ordem de Serviço do status EM_DIAGNOSTICO para RECEBIDA.");
    }

    @Test
    void naoDevePermitirAlterarStatusDeOrdemServicoEntregue() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema");
        // Percorre o fluxo correto até atingir ENTREGUE
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        os.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        os.atualizarStatus(StatusOS.FINALIZADA);
        os.atualizarStatus(StatusOS.ENTREGUE);

        // Quando/Então
        assertThatThrownBy(() -> os.atualizarStatus(StatusOS.FINALIZADA))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Esta ordem de serviço já foi ENTREGUE e não pode mais ser alterada.");
    }

    @Test
    void devePermitirFluxoCompletoDeStatus() {
        // Dado
        OrdemServico os = new OrdemServico(veiculo, "Problema");

        // Quando - Fluxo completo
        assertThat(os.getStatus()).isEqualTo(StatusOS.RECEBIDA);

        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        assertThat(os.getStatus()).isEqualTo(StatusOS.EM_DIAGNOSTICO);

        os.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        assertThat(os.getStatus()).isEqualTo(StatusOS.AGUARDANDO_APROVACAO);

        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        assertThat(os.getStatus()).isEqualTo(StatusOS.EM_EXECUCAO);

        os.atualizarStatus(StatusOS.FINALIZADA);
        assertThat(os.getStatus()).isEqualTo(StatusOS.FINALIZADA);

        os.atualizarStatus(StatusOS.ENTREGUE);
        assertThat(os.getStatus()).isEqualTo(StatusOS.ENTREGUE);
    }
}
