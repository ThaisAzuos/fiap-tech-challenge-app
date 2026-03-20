package com.grupo51.oficinamecanica.comum.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XssValidator Tests")
class XssValidatorTest {

    private XssValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new XssValidator();
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
        assertTrue(validator.isValid("Descrição normal", context));
        assertTrue(validator.isValid("123-456-7890", context));
    }

    @Test
    @DisplayName("Deve rejeitar script tags")
    void shouldRejectScriptTags() {
        assertFalse(validator.isValid("<script>alert('XSS')</script>", context));
        assertFalse(validator.isValid("<SCRIPT>alert('XSS')</SCRIPT>", context));
    }

    @Test
    @DisplayName("Deve rejeitar iframe tags")
    void shouldRejectIframeTags() {
        assertFalse(validator.isValid("<iframe src='malicious.com'></iframe>", context));
    }

    @Test
    @DisplayName("Deve rejeitar event handlers")
    void shouldRejectEventHandlers() {
        assertFalse(validator.isValid("<img onerror='alert(1)'>", context));
        assertFalse(validator.isValid("<body onload='malicious()'>", context));
        assertFalse(validator.isValid("<input onfocus='steal()'>", context));
    }

    @Test
    @DisplayName("Deve rejeitar javascript protocol")
    void shouldRejectJavascriptProtocol() {
        assertFalse(validator.isValid("javascript:alert('XSS')", context));
    }

    @Test
    @DisplayName("Deve aceitar null")
    void shouldAcceptNull() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    @DisplayName("Deve rejeitar SVG com onload")
    void shouldRejectSvgWithOnload() {
        assertFalse(validator.isValid("<svg onload='malicious()'>", context));
    }

    @Test
    @DisplayName("Deve rejeitar caracteres de controle suspeitos")
    void shouldRejectControlCharacters() {
        String stringWithNullByte = "string\0com null byte";
        assertFalse(validator.isValid(stringWithNullByte, context));
    }
}

