package com.grupo51.oficinamecanica.comum.email.service;

import com.grupo51.oficinamecanica.comum.email.config.EmailProperties;
import com.grupo51.oficinamecanica.comum.email.model.EmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("EmailService Tests")
class EmailServiceTest {

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        when(templateEngine.process(any(String.class), any()))
            .thenReturn("<html><body>Email de teste</body></html>");

        EmailProperties emailProperties = new EmailProperties();
        emailProperties.setFrom("noreply@example.com");
        emailProperties.setFromName("Teste");
        emailProperties.setReplyTo("support@example.com");

        emailService = new EmailServiceImpl(mailSender, templateEngine, emailProperties);
    }

    @Test
    @DisplayName("Deve verificar se email está habilitado")
    void shouldCheckIfEmailIsEnabled() {
        // Act
        boolean enabled = emailService.isEmailEnabled();

        // Assert
        assertTrue(enabled);
    }

    @Test
    @DisplayName("Deve adicionar variáveis ao email")
    void shouldAddVariablesToEmail() {
        // Arrange
        EmailRequest request = new EmailRequest("teste@example.com", "Teste", "template");
        
        // Act
        request.addVariable("chave1", "valor1");
        request.addVariable("chave2", 123);

        // Assert
        assertEquals("valor1", request.getVariables().get("chave1"));
        assertEquals(123, request.getVariables().get("chave2"));
    }

    @Test
    @DisplayName("Deve configurar CC e BCC")
    void shouldSetCcAndBcc() {
        // Arrange
        EmailRequest request = new EmailRequest("principal@example.com", "Teste", "template");
        
        // Act
        request.setCc("copia@example.com");
        request.setBcc("copia-oculta@example.com");

        // Assert
        assertEquals("copia@example.com", request.getCc());
        assertEquals("copia-oculta@example.com", request.getBcc());
    }

    @Test
    @DisplayName("Deve criar EmailRequest com dados corretos")
    void shouldCreateEmailRequestWithCorrectData() {
        // Arrange & Act
        EmailRequest request = new EmailRequest("user@example.com", "Assunto", "template");

        // Assert
        assertEquals("user@example.com", request.getTo());
        assertEquals("Assunto", request.getSubject());
        assertEquals("template", request.getTemplateName());
        assertNotNull(request.getVariables());
    }
}

