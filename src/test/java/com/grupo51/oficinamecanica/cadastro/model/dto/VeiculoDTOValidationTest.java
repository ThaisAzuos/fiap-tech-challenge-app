package com.grupo51.oficinamecanica.cadastro.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("VeiculoDTO Validation Tests")
class VeiculoDTOValidationTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Veículo válido deve passar na validação")
    void shouldAcceptValidVeiculo() {
        var veiculo = new VeiculoDTO("ABC1234", "Civic", "Honda", 2020, "Preto", "12345678901");
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(veiculo);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve aceitar placa em novo padrão Mercosul")
    void shouldAcceptNewPlate() {
        var veiculo = new VeiculoDTO("ABC1D23", "Civic", "Honda", 2020, "Preto", "12345678901");
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(veiculo);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar placa inválida")
    void shouldRejectInvalidPlate() {
        var veiculo = new VeiculoDTO("INVALID", "Civic", "Honda", 2020, "Preto", "12345678901");
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(veiculo);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar modelo vazio")
    void shouldRejectEmptyModel() {
        var veiculo = new VeiculoDTO("ABC1234", "", "Honda", 2020, "Preto", "12345678901");
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(veiculo);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar ano menor que 1900")
    void shouldRejectYearBefore1900() {
        var veiculo = new VeiculoDTO("ABC1234", "Civic", "Honda", 1899, "Preto", "12345678901");
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(veiculo);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar modelo com SQL injection")
    void shouldRejectModelWithSqlInjection() {
        var veiculo = new VeiculoDTO("ABC1D23", "'; DROP TABLE--", "Honda", 2020, "Preto", "12345678901");
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(veiculo);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar marca com XSS")
    void shouldRejectBrandWithXss() {
        var veiculo = new VeiculoDTO("ABC1D23", "Civic", "<script>alert(1)</script>", 2020, "Preto", "12345678901");
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(veiculo);
        assertFalse(violations.isEmpty());
    }
}

