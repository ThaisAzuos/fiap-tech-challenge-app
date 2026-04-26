package com.grupo37.oficinamecanica.comum.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SqlInjectionValidator Tests")
class SqlInjectionValidatorTest {

    private SqlInjectionValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new SqlInjectionValidator();
        validator.initialize(null);
        context = Mockito.mock(ConstraintValidatorContext.class);
        var builder = Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
    }

    @Test
    @DisplayName("Deve aceitar strings válidas")
    void shouldAcceptValidStrings() {
        assertTrue(validator.isValid("João Silva", context));
        assertTrue(validator.isValid("123-456-7890", context));
        assertTrue(validator.isValid("usuario@email.com", context));
    }

    @Test
    @DisplayName("Deve rejeitar SQL comments")
    void shouldRejectSqlComments() {
        assertFalse(validator.isValid("'; DROP TABLE users--", context));
        assertFalse(validator.isValid("admin' OR '1'='1", context));
        assertFalse(validator.isValid("'; DELETE FROM users;--", context));
    }

    @Test
    @DisplayName("Deve rejeitar padrões SQL perigosos")
    void shouldRejectSqlKeywords() {
        assertFalse(validator.isValid("SELECT * FROM users", context));
        assertFalse(validator.isValid("UNION SELECT password", context));
        assertFalse(validator.isValid("INSERT INTO malicious", context));
    }

    @Test
    @DisplayName("Deve aceitar null")
    void shouldAcceptNull() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    @DisplayName("Deve rejeitar aspas desbalanceadas")
    void shouldRejectUnbalancedQuotes() {
        assertFalse(validator.isValid("string com aspa desbalanceada'", context));
        assertFalse(validator.isValid("string com \" desbalanceada", context));
    }
}

