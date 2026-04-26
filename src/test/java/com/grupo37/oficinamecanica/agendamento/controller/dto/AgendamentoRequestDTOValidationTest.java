package com.grupo37.oficinamecanica.agendamento.controller.dto;

import com.grupo37.oficinamecanica.agendamento.model.TipoAgendamento;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("AgendamentoRequestDTO Validation Tests")
class AgendamentoRequestDTOValidationTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Agendamento válido deve passar na validação")
    void shouldAcceptValidAgendamento() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(2);
        
        var agendamento = new AgendamentoRequestDTO(
            "12345678901",
            "ABC1234",
            "recurso-123",
            inicio,
            fim,
            TipoAgendamento.ANALISE
        );
        
        Set<ConstraintViolation<AgendamentoRequestDTO>> violations = validator.validate(agendamento);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar cliente ID vazio")
    void shouldRejectEmptyClientId() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(2);
        
        var agendamento = new AgendamentoRequestDTO(
            "",
            "ABC1234",
            "recurso-123",
            inicio,
            fim,
            TipoAgendamento.ANALISE
        );
        
        Set<ConstraintViolation<AgendamentoRequestDTO>> violations = validator.validate(agendamento);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar data de início no passado")
    void shouldRejectPastStartDate() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);
        
        var agendamento = new AgendamentoRequestDTO(
            "12345678901",
            "ABC1234",
            "recurso-123",
            inicio,
            fim,
            TipoAgendamento.ANALISE
        );
        
        Set<ConstraintViolation<AgendamentoRequestDTO>> violations = validator.validate(agendamento);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar data de fim no passado")
    void shouldRejectPastEndDate() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = LocalDateTime.now().minusDays(1);
        
        var agendamento = new AgendamentoRequestDTO(
            "12345678901",
            "ABC1234",
            "recurso-123",
            inicio,
            fim,
            TipoAgendamento.ANALISE
        );
        
        Set<ConstraintViolation<AgendamentoRequestDTO>> violations = validator.validate(agendamento);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar cliente ID com SQL injection")
    void shouldRejectClientIdWithSqlInjection() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(2);
        
        var agendamento = new AgendamentoRequestDTO(
            "'; DROP TABLE agendamentos--",
            "ABC1234",
            "recurso-123",
            inicio,
            fim,
            TipoAgendamento.ANALISE
        );
        
        Set<ConstraintViolation<AgendamentoRequestDTO>> violations = validator.validate(agendamento);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar tipo nulo")
    void shouldRejectNullType() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(2);
        
        var agendamento = new AgendamentoRequestDTO(
            "12345678901",
            "ABC1234",
            "recurso-123",
            inicio,
            fim,
            null
        );
        
        Set<ConstraintViolation<AgendamentoRequestDTO>> violations = validator.validate(agendamento);
        assertFalse(violations.isEmpty());
    }
}

