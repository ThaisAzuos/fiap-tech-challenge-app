package com.grupo37.oficinamecanica.estoque.service;

import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import com.grupo37.oficinamecanica.estoque.controller.PecaDTO;
import com.grupo37.oficinamecanica.estoque.model.Peca;
import com.grupo37.oficinamecanica.estoque.repository.PecaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PecaServiceTest {

    @Mock
    private PecaRepository pecaRepository;

    private PecaService pecaService;

    @BeforeEach
    void setUp() {
        pecaService = new PecaService(pecaRepository);
    }

    @Test
    void deveSalvarPecaComPrecoPositivo() {
        PecaDTO dto = new PecaDTO("Filtro de óleo", new BigDecimal("45.90"), 10);
        Peca pecaSalva = new Peca("Filtro de óleo", new BigDecimal("45.90"), 10);
        when(pecaRepository.save(any(Peca.class))).thenReturn(pecaSalva);

        Peca resultado = pecaService.salvarPeca(dto);

        assertThat(resultado.getNome()).isEqualTo("Filtro de óleo");
        verify(pecaRepository).save(any(Peca.class));
    }

    @Test
    void deveLancarExcecaoQuandoPrecoForZero() {
        PecaDTO dto = new PecaDTO("Filtro", BigDecimal.ZERO, 5);

        assertThatThrownBy(() -> pecaService.salvarPeca(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("maior que zero");
    }

    @Test
    void deveLancarExcecaoQuandoPrecoForNegativo() {
        PecaDTO dto = new PecaDTO("Filtro", new BigDecimal("-10.00"), 5);

        assertThatThrownBy(() -> pecaService.salvarPeca(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("maior que zero");
    }

    @Test
    void deveListarTodasAsPecas() {
        List<Peca> pecas = List.of(
                new Peca("Vela de ignição", new BigDecimal("12.50"), 20),
                new Peca("Filtro de ar", new BigDecimal("35.00"), 15)
        );
        when(pecaRepository.findAll()).thenReturn(pecas);

        List<Peca> resultado = pecaService.listarTodas();

        assertThat(resultado).hasSize(2);
        verify(pecaRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaPecas() {
        when(pecaRepository.findAll()).thenReturn(List.of());

        List<Peca> resultado = pecaService.listarTodas();

        assertThat(resultado).isEmpty();
    }
}

