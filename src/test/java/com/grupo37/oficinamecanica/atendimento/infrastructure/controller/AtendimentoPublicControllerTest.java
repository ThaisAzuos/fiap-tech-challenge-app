package com.grupo37.oficinamecanica.atendimento.infrastructure.controller;

import com.grupo37.oficinamecanica.atendimento.application.usecase.AtendimentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AtendimentoPublicControllerTest {

    @Mock
    private AtendimentoService atendimentoService;

    private AtendimentoPublicController controller;

    @BeforeEach
    void setUp() {
        controller = new AtendimentoPublicController(atendimentoService);
    }

    @Test
    void deveAprovarOrcamentoPublicoComSucesso() {
        UUID osId = UUID.randomUUID();

        ResponseEntity<String> response = controller.aprovarOrcamentoPublico(osId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Orçamento aprovado com sucesso");
        verify(atendimentoService).aprovarOrcamento(osId);
    }
}

