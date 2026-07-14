package com.grupo37.oficinamecanica.agendamento.controller;

import com.grupo37.oficinamecanica.agendamento.controller.dto.AgendamentoRequestDTO;
import com.grupo37.oficinamecanica.agendamento.model.Agendamento;
import com.grupo37.oficinamecanica.agendamento.model.TipoAgendamento;
import com.grupo37.oficinamecanica.agendamento.service.AgendamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoControllerTest {

    @Mock
    private AgendamentoService service;

    @Mock
    private Agendamento agendamento;

    private AgendamentoController controller;

    @BeforeEach
    void setUp() {
        controller = new AgendamentoController(service);
    }

    @Test
    void deveRealizarAgendamentoERetornar201() {
        UUID id = UUID.randomUUID();
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(
                "73383053036",
                "ABC1D23",
                "09151522037",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                TipoAgendamento.ANALISE
        );
        when(agendamento.getId()).thenReturn(id);
        when(service.agendar(any(Agendamento.class))).thenReturn(agendamento);

        ResponseEntity<?> response = controller.realizarAgendamento(dto, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service).agendar(any(Agendamento.class));
    }

    @Test
    void deveDetalharAgendamentoPorId() {
        UUID id = UUID.randomUUID();
        when(service.buscarPorId(id)).thenReturn(Optional.of(agendamento));

        ResponseEntity<?> response = controller.detalhar(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(agendamento);
        verify(service).buscarPorId(id);
    }

    @Test
    void deveRetornarNullQuandoAgendamentoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(service.buscarPorId(id)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.detalhar(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }
}

