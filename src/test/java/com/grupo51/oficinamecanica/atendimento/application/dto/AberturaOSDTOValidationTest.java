package com.grupo51.oficinamecanica.atendimento.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("AberturaOSDTO Validation Tests")
class AberturaOSDTOValidationTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Abertura OS válida deve passar na validação")
    void shouldAcceptValidAberturaOS() {
        var abertura = new AberturaOSDTO("ABC1D23", "Barulho estranho no motor e problema na suspensão");
        Set<ConstraintViolation<AberturaOSDTO>> violations = validator.validate(abertura);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve aceitar placa em padrão Mercosul")
    void shouldAcceptMercosulPlate() {
        var abertura = new AberturaOSDTO("XYZ9K45", "Barulho estranho no motor e problema na suspensão");
        Set<ConstraintViolation<AberturaOSDTO>> violations = validator.validate(abertura);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar placa inválida")
    void shouldRejectInvalidPlate() {
        var abertura = new AberturaOSDTO("INVALID", "Barulho estranho no motor");
        Set<ConstraintViolation<AberturaOSDTO>> violations = validator.validate(abertura);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar descrição vazia")
    void shouldRejectEmptyDescription() {
        var abertura = new AberturaOSDTO("ABC1D23", "");
        Set<ConstraintViolation<AberturaOSDTO>> violations = validator.validate(abertura);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar descrição muito curta")
    void shouldRejectShortDescription() {
        var abertura = new AberturaOSDTO("ABC1D23", "Problema");
        Set<ConstraintViolation<AberturaOSDTO>> violations = validator.validate(abertura);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar descrição com SQL injection")
    void shouldRejectDescriptionWithSqlInjection() {
        var abertura = new AberturaOSDTO("ABC1D23", "'; DROP TABLE ordens--problemas");
        Set<ConstraintViolation<AberturaOSDTO>> violations = validator.validate(abertura);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar descrição com XSS")
    void shouldRejectDescriptionWithXss() {
        var abertura = new AberturaOSDTO("ABC1D23", "<script>alert('XSS')</script> pista do problema");
        Set<ConstraintViolation<AberturaOSDTO>> violations = validator.validate(abertura);
        assertFalse(violations.isEmpty());
    }
}

