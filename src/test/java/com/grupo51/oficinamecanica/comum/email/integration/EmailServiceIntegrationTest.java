package com.grupo51.oficinamecanica.comum.email.integration;

import com.grupo51.oficinamecanica.comum.email.config.EmailProperties;
import com.grupo51.oficinamecanica.comum.email.model.EmailRequest;
import com.grupo51.oficinamecanica.comum.email.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de Integração para EmailService com MailHog
 * 
 * Testes que verificam o envio real de emails através do MailHog
 * para validar templates, renderização Thymeleaf e fluxo end-to-end.
 * 
 * Requisito: MailHog deve estar rodando em localhost:1025 (SMTP) e 8025 (Web UI)
 */
@SpringBootTest
@ActiveProfiles("dev")
@DisplayName("Testes de Integração - EmailService com MailHog")
class EmailServiceIntegrationTest {

    private static final int SMTP_CHECK_TIMEOUT_MS = 800;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailProperties emailProperties;

    @Value("${spring.mail.host:localhost}")
    private String smtpHost;

    @Value("${spring.mail.port:1025}")
    private int smtpPort;

    private EmailRequest emailRequest;

    @BeforeEach
    void setUp() {
        Assumptions.assumeTrue(isSmtpAvailable(),
                () -> String.format("SMTP indisponivel em %s:%d. Inicie o MailHog para executar este teste.", smtpHost, smtpPort));

        emailRequest = new EmailRequest(
            "cliente@teste.com",
            "Teste de Email",
            "email/ordem-servico-criada"
        );
    }

    private boolean isSmtpAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(smtpHost, smtpPort), SMTP_CHECK_TIMEOUT_MS);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Test
    @DisplayName("Deve enviar email de criação de ordem de serviço com sucesso")
    void shouldSendEmailOrdenServioCriada() {
        // Arrange
        emailRequest.addVariable("ordemServicoId", "UUID-12345");
        emailRequest.addVariable("status", "AGUARDANDO_APROVACAO");
        emailRequest.addVariable("dataAbertura", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        emailRequest.addVariable("veiculo", "Honda Civic 2020");
        emailRequest.addVariable("placa", "ABC1D23");
        emailRequest.addVariable("descricaoProblema", "Problema de suspensão dianteira");
        emailRequest.addVariable("currentYear", "2026");

        // Act
        boolean resultado = emailService.sendEmail(emailRequest);

        // Assert
        assertTrue(resultado, "Email deveria ser enviado com sucesso");
    }

    @Test
    @DisplayName("Deve enviar email de atualização de status de ordem de serviço")
    void shouldSendEmailAtualizacaoStatus() {
        // Arrange
        emailRequest = new EmailRequest(
            "cliente@teste.com",
            "Atualização: Ordem de Serviço #12345",
            "email/ordem-servico-atualizada"
        );
        
        emailRequest.addVariable("ordemServicoId", "UUID-12345");
        emailRequest.addVariable("novoStatus", "EM_EXECUÇÃO");
        emailRequest.addVariable("dataAtualizacao", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        emailRequest.addVariable("veiculo", "Honda Civic 2020");
        emailRequest.addVariable("placa", "ABC1D23");
        emailRequest.addVariable("observacoes", "Serviço iniciado com sucesso");
        emailRequest.addVariable("currentYear", "2026");

        // Act
        boolean resultado = emailService.sendEmail(emailRequest);

        // Assert
        assertTrue(resultado, "Email de atualização deveria ser enviado com sucesso");
    }

    @Test
    @DisplayName("Deve validar que email não é enviado sem destinatário")
    void shouldNotSendEmailWithoutRecipient() {
        // Arrange
        EmailRequest invalidRequest = new EmailRequest(
            "",
            "Teste",
            "email/ordem-servico-criada"
        );

        // Act
        boolean resultado = emailService.sendEmail(invalidRequest);

        // Assert
        assertFalse(resultado, "Email não deveria ser enviado sem destinatário válido");
    }

    @Test
    @DisplayName("Deve validar que email não é enviado sem template")
    void shouldNotSendEmailWithoutTemplate() {
        // Arrange
        EmailRequest invalidRequest = new EmailRequest(
            "cliente@teste.com",
            "Teste",
            ""
        );

        // Act
        boolean resultado = emailService.sendEmail(invalidRequest);

        // Assert
        assertFalse(resultado, "Email não deveria ser enviado sem template");
    }

    @Test
    @DisplayName("Deve enviar email com CC")
    void shouldSendEmailWithCC() {
        // Arrange
        emailRequest.setCc("copia@teste.com");
        emailRequest.addVariable("ordemServicoId", "UUID-12345");
        emailRequest.addVariable("status", "AGUARDANDO_APROVACAO");
        emailRequest.addVariable("dataAbertura", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        emailRequest.addVariable("veiculo", "Honda Civic 2020");
        emailRequest.addVariable("placa", "ABC1D23");
        emailRequest.addVariable("descricaoProblema", "Problema de suspensão dianteira");
        emailRequest.addVariable("currentYear", "2026");

        // Act
        boolean resultado = emailService.sendEmail(emailRequest);

        // Assert
        assertTrue(resultado, "Email com CC deveria ser enviado com sucesso");
    }

    @Test
    @DisplayName("Deve enviar email com BCC")
    void shouldSendEmailWithBCC() {
        // Arrange
        emailRequest.setBcc("auditoria@teste.com");
        emailRequest.addVariable("ordemServicoId", "UUID-12345");
        emailRequest.addVariable("status", "AGUARDANDO_APROVACAO");
        emailRequest.addVariable("dataAbertura", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        emailRequest.addVariable("veiculo", "Honda Civic 2020");
        emailRequest.addVariable("placa", "ABC1D23");
        emailRequest.addVariable("descricaoProblema", "Problema de suspensão dianteira");
        emailRequest.addVariable("currentYear", "2026");

        // Act
        boolean resultado = emailService.sendEmail(emailRequest);

        // Assert
        assertTrue(resultado, "Email com BCC deveria ser enviado com sucesso");
    }

    @Test
    @DisplayName("Deve validar que EmailService está habilitado")
    void shouldValidateEmailServiceIsEnabled() {
        // Act & Assert
        assertTrue(emailService.isEmailEnabled(), "EmailService deveria estar habilitado no perfil dev");
    }

    @Test
    @DisplayName("Deve enviar email de cancelamento de ordem de serviço")
    void shouldSendEmailCancelamento() {
        // Arrange
        emailRequest = new EmailRequest(
            "cliente@teste.com",
            "Ordem de Serviço #12345 - CANCELADA",
            "email/ordem-servico-cancelada"
        );
        
        emailRequest.addVariable("ordemServicoId", "UUID-12345");
        emailRequest.addVariable("dataCancelamento", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        emailRequest.addVariable("motivoCancelamento", "Problemas com a oficina, procurar alternativa");
        emailRequest.addVariable("veiculo", "Honda Civic 2020");
        emailRequest.addVariable("placa", "ABC1D23");
        emailRequest.addVariable("currentYear", "2026");

        // Act
        boolean resultado = emailService.sendEmail(emailRequest);

        // Assert
        assertTrue(resultado, "Email de cancelamento deveria ser enviado com sucesso");
    }

    @Test
    @DisplayName("Deve enviar email de conclusão de ordem de serviço")
    void shouldSendEmailConclusao() {
        // Arrange
        emailRequest = new EmailRequest(
            "cliente@teste.com",
            "Sua Ordem de Serviço #12345 foi Concluída!",
            "email/ordem-servico-concluida"
        );
        
        emailRequest.addVariable("ordemServicoId", "UUID-12345");
        emailRequest.addVariable("dataConclusao", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        emailRequest.addVariable("veiculo", "Honda Civic 2020");
        emailRequest.addVariable("placa", "ABC1D23");
        emailRequest.addVariable("valorTotal", "1350.00");
        emailRequest.addVariable("quilometragem", "45.230 km");
        emailRequest.addVariable("currentYear", "2026");

        // Act
        boolean resultado = emailService.sendEmail(emailRequest);

        // Assert
        assertTrue(resultado, "Email de conclusão deveria ser enviado com sucesso");
    }

    @Test
    @DisplayName("Deve validar properties de email configuradas")
    void shouldValidateEmailPropertiesConfigured() {
        assertNotNull(emailProperties.getFrom(), "Email 'from' deveria estar configurado");
        assertNotNull(emailProperties.getFromName(), "Email 'fromName' deveria estar configurado");
        assertNotNull(emailProperties.getReplyTo(), "Email 'replyTo' deveria estar configurado");

        assertEquals("noreply@oficinamecanica.com", emailProperties.getFrom());
        assertEquals("Oficina Mecânica", emailProperties.getFromName());
        assertEquals("suporte@oficinamecanica.com", emailProperties.getReplyTo());
    }
}
