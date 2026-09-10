package com.grupo37.oficinamecanica.comum.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornar400ParaIllegalArgumentException() {
        var ex = new IllegalArgumentException("CPF inválido");
        ResponseEntity<String> response = handler.handleValidationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("CPF inválido");
    }

    @Test
    void deveRetornar422ParaBusinessException() {
        var ex = new BusinessException("Regra de negócio violada");
        ResponseEntity<String> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isEqualTo("Regra de negócio violada");
    }

    @Test
    void deveRetornar409ParaBusinessExceptionRecursoOcupado() {
        var ex = new BusinessException("Recurso ocupado: horário indisponível");
        ResponseEntity<String> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void deveRetornar409ParaDataIntegrityViolationGenerico() {
        var ex = new DataIntegrityViolationException("constraint violation");
        ResponseEntity<String> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("Conflito de dados");
    }

    @Test
    void deveRetornarMensagemEmailDuplicadoParaDataIntegrity() {
        var cause = new RuntimeException("duplicate key value violates unique constraint on (email)=(test@test.com)");
        var ex = new DataIntegrityViolationException("msg", cause);
        ResponseEntity<String> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("e-mail");
    }

    @Test
    void deveRetornarMensagemCpfDuplicadoParaDataIntegrity() {
        var cause = new RuntimeException("duplicate key value violates unique constraint on (cpf)=(12345678900)");
        var ex = new DataIntegrityViolationException("msg", cause);
        ResponseEntity<String> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("CPF");
    }

    @Test
    void deveRetornarMensagemRegistroFuncionalDuplicadoParaDataIntegrity() {
        var cause = new RuntimeException("duplicate key violates unique constraint on (registro_funcional)=(RF001)");
        var ex = new DataIntegrityViolationException("msg", cause);
        ResponseEntity<String> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("registro funcional");
    }
}

