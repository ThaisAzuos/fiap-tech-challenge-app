package com.grupo37.oficinamecanica.agendamento.service;

import com.grupo37.oficinamecanica.agendamento.model.Agendamento;
import com.grupo37.oficinamecanica.agendamento.model.JanelaServico;
import com.grupo37.oficinamecanica.agendamento.model.TipoAgendamento;
import com.grupo37.oficinamecanica.agendamento.repository.AgendamentoRepository;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository repository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Test
    void deveAgendarQuandoJanelaEstiverDisponivel() {
        Agendamento agendamento = novoAgendamento();
        when(repository.existsByRecursoIdAndJanelaOverlap(any(), any(), any())).thenReturn(false);
        when(repository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agendamento resultado = agendamentoService.agendar(agendamento);

        assertThat(resultado.isConfirmado()).isTrue();
        verify(repository).save(agendamento);
    }

    @Test
    void deveLancarExcecaoQuandoRecursoEstiverOcupado() {
        Agendamento agendamento = novoAgendamento();
        when(repository.existsByRecursoIdAndJanelaOverlap(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> agendamentoService.agendar(agendamento))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Recurso ocupado para esta Janela de Serviço.");

        verify(repository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveBuscarAgendamentoPorId() {
        UUID id = UUID.randomUUID();
        Agendamento agendamento = novoAgendamento();
        when(repository.findById(id)).thenReturn(Optional.of(agendamento));

        Optional<Agendamento> resultado = agendamentoService.buscarPorId(id);

        assertThat(resultado).contains(agendamento);
        verify(repository).findById(id);
    }

    private Agendamento novoAgendamento() {
        JanelaServico janela = new JanelaServico(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 12, 0)
        );

        Agendamento agendamento = new Agendamento();
        agendamento.setClienteId("12345678901");
        agendamento.setVeiculoId("ABC1D23");
        agendamento.setRecursoId("MEC-01");
        agendamento.setTipo(TipoAgendamento.ANALISE);
        agendamento.setJanela(janela);
        return agendamento;
    }
}

