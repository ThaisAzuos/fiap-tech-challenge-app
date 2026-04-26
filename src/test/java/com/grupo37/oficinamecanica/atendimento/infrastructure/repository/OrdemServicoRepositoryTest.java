package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.cadastro.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.model.Cpf;
import com.grupo37.oficinamecanica.cadastro.model.Email;
import com.grupo37.oficinamecanica.cadastro.model.Endereco;
import com.grupo37.oficinamecanica.cadastro.model.Placa;
import com.grupo37.oficinamecanica.cadastro.model.Veiculo;
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

    @Test
    void deveRetornarPaginaVaziaQuandoNaoHaOrdensAtivas() {
        // Dado
        Pageable pageable = PageRequest.of(0, 10);

        // Quando
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Então
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void deveSuportarPaginacaoNaListagemAtiva() {
        // Dado
        Pageable pageable = PageRequest.of(0, 5);

        // Quando
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Então
        assertThat(result.getPageable().getPageSize()).isEqualTo(5);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    }

    @Test
    void deveBuscarOsComDetalhesMesmoSemItens() {
        Cliente cliente = new Cliente(
                "Cliente Teste",
                new Cpf("25390437021"),
                new Email("cliente.teste@oficina.local"),
                "11999999999",
                new Endereco("Rua A", "100", "", "Centro", "Sao Paulo", "SP", "01001000")
        );
        entityManager.persist(cliente);

        Veiculo veiculo = new Veiculo(
                new Placa("ABC1D23"),
                "Uno",
                "Fiat",
                2020,
                "Branco",
                cliente
        );
        entityManager.persist(veiculo);

        OrdemServico os = new OrdemServico(veiculo, "Teste sem itens");
        entityManager.persist(os);
        entityManager.flush();
        entityManager.clear();

        Optional<OrdemServico> result = ordemServicoRepository.findByIdWithDetails(os.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(os.getId());
        assertThat(result.get().getItens()).isEmpty();
        assertThat(result.get().getVeiculo().getPlaca()).isEqualTo("ABC1D23");
        assertThat(result.get().getVeiculo().getDono().getCpf()).isEqualTo("25390437021");
    }
}
