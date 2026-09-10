package com.grupo37.oficinamecanica.estoque.controller;

import com.grupo37.oficinamecanica.estoque.model.Peca;
import com.grupo37.oficinamecanica.estoque.service.PecaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PecaControllerTest {

    @Mock
    private PecaService pecaService;

    private PecaController controller;

    @BeforeEach
    void setUp() {
        controller = new PecaController(pecaService);
    }

    @Test
    void deveCriarPecaERetornar201() {
        PecaDTO dto = new PecaDTO("Filtro de óleo", new BigDecimal("45.90"), 10);
        Peca pecaSalva = new Peca("Filtro de óleo", new BigDecimal("45.90"), 10);
        when(pecaService.salvarPeca(any(PecaDTO.class))).thenReturn(pecaSalva);

        ResponseEntity<Peca> response = controller.criarPeca(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getNome()).isEqualTo("Filtro de óleo");
        verify(pecaService).salvarPeca(dto);
    }

    @Test
    void deveListarTodasAsPecas() {
        List<Peca> pecas = List.of(
                new Peca("Vela", new BigDecimal("12.50"), 20),
                new Peca("Filtro", new BigDecimal("35.00"), 15)
        );
        when(pecaService.listarTodas()).thenReturn(pecas);

        ResponseEntity<List<Peca>> response = controller.listar();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        verify(pecaService).listarTodas();
    }
}

