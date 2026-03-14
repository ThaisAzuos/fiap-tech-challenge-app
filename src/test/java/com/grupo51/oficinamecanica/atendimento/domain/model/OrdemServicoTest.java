package com.grupo51.oficinamecanica.atendimento.domain.model;

import com.grupo51.oficinamecanica.cadastro.model.Cliente;
import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import com.grupo51.oficinamecanica.estoque.model.Peca;
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
        // Create mocks for dependencies
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
        // Given
        String descricaoProblema = "Problema no freio";

        // When
        OrdemServico os = new OrdemServico(veiculo, descricaoProblema);

        // Then
        assertThat(os.getVeiculo()).isEqualTo(veiculo);
        assertThat(os.getDescricaoProblema()).isEqualTo(descricaoProblema);
        assertThat(os.getStatus()).isEqualTo(StatusOS.RECEBIDA);
        assertThat(os.getDataAbertura()).isNotNull();
        assertThat(os.getValorTotal()).isEqualTo(BigDecimal.ZERO);
        assertThat(os.getItens()).isEmpty();
    }

    @Test
    void deveAdicionarPecaNaOrdemServico() {
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema no freio");
        int quantidade = 2;

        // When
        os.adicionarPeca(peca1, quantidade);

        // Then
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
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema geral");

        // When
        os.adicionarPeca(peca1, 1); // 150.00
        os.adicionarPeca(peca2, 2); // 160.00

        // Then
        assertThat(os.getValorTotal()).isEqualTo(BigDecimal.valueOf(310.00));
        assertThat(os.getItens()).hasSize(2);
    }

    @Test
    void naoDevePermitirAdicionarPecaEmOrdemServicoFinalizada() {
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema");
        // Go through proper flow to reach FINALIZADA
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        os.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        os.atualizarStatus(StatusOS.FINALIZADA);

        // When/Then
        assertThatThrownBy(() -> os.adicionarPeca(peca1, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível alterar uma OS já encerrada.");
    }

    @Test
    void naoDevePermitirAdicionarPecaEmOrdemServicoEntregue() {
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema");
        // Go through proper flow to reach ENTREGUE
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        os.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        os.atualizarStatus(StatusOS.FINALIZADA);
        os.atualizarStatus(StatusOS.ENTREGUE);

        // When/Then
        assertThatThrownBy(() -> os.adicionarPeca(peca1, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível alterar uma OS já encerrada.");
    }

    @Test
    void devePermitirTransicaoDeStatusValida() {
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema");

        // When
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // Then
        assertThat(os.getStatus()).isEqualTo(StatusOS.EM_DIAGNOSTICO);
    }

    @Test
    void naoDevePermitirTransicaoDiretaDeRecebidaParaFinalizada() {
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema");

        // When/Then
        assertThatThrownBy(() -> os.atualizarStatus(StatusOS.FINALIZADA))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A OS precisa passar por diagnóstico antes de ser finalizada ou entregue.");
    }

    @Test
    void naoDevePermitirRetrocessoDeStatus() {
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema");
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);

        // When/Then
        assertThatThrownBy(() -> os.atualizarStatus(StatusOS.RECEBIDA))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não é permitido retornar a Ordem de Serviço para um status anterior");
    }

    @Test
    void naoDevePermitirAlterarStatusDeOrdemServicoEntregue() {
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema");
        // Go through proper flow to reach ENTREGUE
        os.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        os.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        os.atualizarStatus(StatusOS.EM_EXECUCAO);
        os.atualizarStatus(StatusOS.FINALIZADA);
        os.atualizarStatus(StatusOS.ENTREGUE);

        // When/Then
        assertThatThrownBy(() -> os.atualizarStatus(StatusOS.FINALIZADA))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Esta ordem de serviço já foi entregue e não pode mais ser alterada.");
    }

    @Test
    void devePermitirFluxoCompletoDeStatus() {
        // Given
        OrdemServico os = new OrdemServico(veiculo, "Problema");

        // When - Fluxo completo
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
