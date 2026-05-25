package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.ClienteEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
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
        Page<OrdemServicoEntity> result = ordemServicoRepository.findAllAtivas(pageable);

        // Então
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void deveSuportarPaginacaoNaListagemAtiva() {
        // Dado
        Pageable pageable = PageRequest.of(0, 5);

        // Quando
        Page<OrdemServicoEntity> result = ordemServicoRepository.findAllAtivas(pageable);

        // Então
        assertThat(result.getPageable().getPageSize()).isEqualTo(5);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    }
}
