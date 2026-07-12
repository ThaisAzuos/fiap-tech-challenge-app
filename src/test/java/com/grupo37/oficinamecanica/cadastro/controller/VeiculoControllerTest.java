package com.grupo37.oficinamecanica.cadastro.controller;

import com.grupo37.oficinamecanica.cadastro.controller.dto.VeiculoDTO;
import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.grupo37.oficinamecanica.cadastro.service.VeiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

    @Mock
    private VeiculoService veiculoService;

    @Mock
    private Veiculo veiculoMock;

    private VeiculoController controller;

    @BeforeEach
    void setUp() {
        controller = new VeiculoController(veiculoService);
    }

    @Test
    void deveCriarVeiculoERetornar201() {
        VeiculoDTO dto = new VeiculoDTO("DEF4G56", "Corolla", "Toyota", 2023, "Preto", "73383053036");
        when(veiculoMock.getPlaca()).thenReturn("DEF4G56");
        when(veiculoService.salvarVeiculo(any(VeiculoDTO.class))).thenReturn(veiculoMock);

        ResponseEntity<Veiculo> response = controller.criarVeiculo(dto, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        verify(veiculoService).salvarVeiculo(dto);
    }

    @Test
    void deveListarVeiculosPorCpfDono() {
        when(veiculoService.listarPorCpfDono("73383053036")).thenReturn(List.of(veiculoMock));

        ResponseEntity<List<Veiculo>> response = controller.listarPorDono("73383053036");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(veiculoService).listarPorCpfDono("73383053036");
    }
}

