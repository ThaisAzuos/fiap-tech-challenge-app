package com.grupo51.oficinamecanica.estoque.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("PecaDTO Validation Tests")
class PecaDTOValidationTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Peça válida deve passar na validação")
    void shouldAcceptValidPeca() {
        var peca = new PecaDTO("Corrente de Distribuição", BigDecimal.valueOf(250.50), 15);
        Set<ConstraintViolation<PecaDTO>> violations = validator.validate(peca);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome vazio")
    void shouldRejectEmptyName() {
        var peca = new PecaDTO("", BigDecimal.valueOf(250.50), 15);
        Set<ConstraintViolation<PecaDTO>> violations = validator.validate(peca);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome muito curto")
    void shouldRejectShortName() {
        var peca = new PecaDTO("XY", BigDecimal.valueOf(250.50), 15);
        Set<ConstraintViolation<PecaDTO>> violations = validator.validate(peca);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar preço zero")
    void shouldRejectZeroPrice() {
        var peca = new PecaDTO("Corrente", BigDecimal.ZERO, 15);
        Set<ConstraintViolation<PecaDTO>> violations = validator.validate(peca);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar preço negativo")
    void shouldRejectNegativePrice() {
        var peca = new PecaDTO("Corrente", BigDecimal.valueOf(-50), 15);
        Set<ConstraintViolation<PecaDTO>> violations = validator.validate(peca);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar quantidade negativa")
    void shouldRejectNegativeQuantity() {
        var peca = new PecaDTO("Corrente", BigDecimal.valueOf(250.50), -5);
        Set<ConstraintViolation<PecaDTO>> violations = validator.validate(peca);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome com SQL injection")
    void shouldRejectNameWithSqlInjection() {
        var peca = new PecaDTO("'; DROP TABLE pecas--", BigDecimal.valueOf(250.50), 15);
        Set<ConstraintViolation<PecaDTO>> violations = validator.validate(peca);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome com XSS")
    void shouldRejectNameWithXss() {
        var peca = new PecaDTO("<img onerror='alert(1)'>", BigDecimal.valueOf(250.50), 15);
        Set<ConstraintViolation<PecaDTO>> violations = validator.validate(peca);
        assertFalse(violations.isEmpty());
    }
}

