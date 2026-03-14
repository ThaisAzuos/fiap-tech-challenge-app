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
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void deveSuportarPaginacaoNaListagemAtiva() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);

        // When
        Page<OrdemServico> result = ordemServicoRepository.findAllAtivas(pageable);

        // Then
        assertThat(result.getPageable().getPageSize()).isEqualTo(5);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    }
}
