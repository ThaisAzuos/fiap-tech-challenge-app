package com.grupo51.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo51.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo51.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo51.oficinamecanica.cadastro.model.Cliente;
import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrdemServicoRepositoryTest {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cliente cliente;
    private Veiculo veiculo;
    private OrdemServico osAtiva;
    private OrdemServico osFinalizada;
    private OrdemServico osEntregue;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("João Silva", "12345678901", "joao@email.com");
        veiculo = new Veiculo("ABC1234", "Fiat Uno", "Prata", 2010, cliente);

        entityManager.persist(cliente);
        entityManager.persist(veiculo);

        // OS ativa (EM_DIAGNOSTICO)
        osAtiva = new OrdemServico(veiculo, "Problema no freio");
        osAtiva.atualizarStatus(StatusOS.EM_DIAGNOSTICO);
        entityManager.persist(osAtiva);

        // OS finalizada
        osFinalizada = new OrdemServico(veiculo, "Troca de óleo");
        osFinalizada.atualizarStatus(StatusOS.FINALIZADA);
        entityManager.persist(osFinalizada);

        // OS entregue
        osEntregue = new OrdemServico(veiculo, "Revisão geral");
        osEntregue.atualizarStatus(StatusOS.ENTREGUE);
        entityManager.persist(osEntregue);

        entityManager.flush();
    }

    @Test
    void deveEncontrarOrdemServicoComDetalhes() {
        // When
        Optional<OrdemServico> result = ordemServicoRepository.findByIdWithDetails(osAtiva.getId());

        // Then
        assertThat(result).isPresent();
        OrdemServico os = result.get();
        assertThat(os.getId()).isEqualTo(osAtiva.getId());
        assertThat(os.getVeiculo()).isNotNull();
        assertThat(os.getVeiculo().getDono()).isNotNull();
        assertThat(os.getItens()).isNotNull();
    }

    @Test
    void deveRetornarVazioQuandoIdNaoExiste() {
        // Given
        var idInexistente = java.util.UUID.randomUUID();

        // When
        Optional<OrdemServico> result = ordemServicoRepository.findByIdWithDetails(idInexistente);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void deveListarApenasOrdensServicoAtivas() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(osAtiva.getId());
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(StatusOS.EM_DIAGNOSTICO);
    }

    @Test
    void deveExcluirOrdensServicoFinalizadasDaListagemAtiva() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Then
        assertThat(result.getContent())
                .noneMatch(os -> os.getStatus() == StatusOS.FINALIZADA)
                .noneMatch(os -> os.getStatus() == StatusOS.ENTREGUE);
    }

    @Test
    void deveExcluirOrdensServicoEntreguesDaListagemAtiva() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Then
        assertThat(result.getContent())
                .noneMatch(os -> os.getStatus() == StatusOS.ENTREGUE);
    }

    @Test
    void deveSuportarPaginacaoNaListagemAtiva() {
        // Given - Criar mais OS ativas
        OrdemServico osAtiva2 = new OrdemServico(veiculo, "Problema na suspensão");
        osAtiva2.atualizarStatus(StatusOS.AGUARDANDO_APROVACAO);
        entityManager.persist(osAtiva2);

        OrdemServico osAtiva3 = new OrdemServico(veiculo, "Problema elétrico");
        osAtiva3.atualizarStatus(StatusOS.EM_EXECUCAO);
        entityManager.persist(osAtiva3);

        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 2);
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void deveCarregarRelacionamentosNaListagemAtiva() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Then
        assertThat(result.getContent()).isNotEmpty();
        OrdemServico os = result.getContent().get(0);
        assertThat(os.getVeiculo()).isNotNull();
        assertThat(os.getVeiculo().getDono()).isNotNull();
        assertThat(os.getVeiculo().getPlaca()).isEqualTo("ABC1234");
        assertThat(os.getVeiculo().getDono().getNome()).isEqualTo("João Silva");
    }
}
