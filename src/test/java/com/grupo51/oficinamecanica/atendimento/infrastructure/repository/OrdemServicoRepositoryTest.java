package com.grupo51.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo51.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo51.oficinamecanica.atendimento.domain.model.StatusOS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrdemServicoRepositoryTest {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

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
}
