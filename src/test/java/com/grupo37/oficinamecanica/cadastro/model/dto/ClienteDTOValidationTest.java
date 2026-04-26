package com.grupo37.oficinamecanica.cadastro.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ClienteDTO Validation Tests")
class ClienteDTOValidationTest {

    @Autowired
    private Validator validator;

    private EnderecoDTO endereco;

    @BeforeEach
    void setUp() {
        endereco = new EnderecoDTO("Rua Test", "123", "Apto 1", "Bairro", "São Paulo", "SP", "01234-567");
    }

    @Test
    @DisplayName("Cliente válido deve passar na validação")
    void shouldAcceptValidCliente() {
        var cliente = new ClienteDTO(
            "João Silva",
            "12345678901",
            "joao@email.com",
            "11987654321",
            endereco
        );

        Set<ConstraintViolation<ClienteDTO>> violations = validator.validate(cliente);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome vazio")
    void shouldRejectEmptyName() {
        var cliente = new ClienteDTO("", "12345678901", "joao@email.com", "11987654321", endereco);
        Set<ConstraintViolation<ClienteDTO>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome muito curto")
    void shouldRejectShortName() {
        var cliente = new ClienteDTO("Jo", "12345678901", "joao@email.com", "11987654321", endereco);
        Set<ConstraintViolation<ClienteDTO>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar CPF com menos de 11 dígitos")
    void shouldRejectInvalidCpf() {
        var cliente = new ClienteDTO("João Silva", "123456789", "joao@email.com", "11987654321", endereco);
        Set<ConstraintViolation<ClienteDTO>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar email inválido")
    void shouldRejectInvalidEmail() {
        var cliente = new ClienteDTO("João Silva", "12345678901", "email-invalido", "11987654321", endereco);
        Set<ConstraintViolation<ClienteDTO>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar telefone com caracteres não numéricos")
    void shouldRejectPhoneWithLetters() {
        var cliente = new ClienteDTO("João Silva", "12345678901", "joao@email.com", "119876ABC21", endereco);
        Set<ConstraintViolation<ClienteDTO>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome com SQL injection")
    void shouldRejectNameWithSqlInjection() {
        var cliente = new ClienteDTO("'; DROP TABLE--", "12345678901", "joao@email.com", "11987654321", endereco);
        Set<ConstraintViolation<ClienteDTO>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome com XSS")
    void shouldRejectNameWithXss() {
        var cliente = new ClienteDTO("<script>alert('XSS')</script>", "12345678901", "joao@email.com", "11987654321", endereco);
        Set<ConstraintViolation<ClienteDTO>> violations = validator.validate(cliente);
        assertFalse(violations.isEmpty());
    }
}

