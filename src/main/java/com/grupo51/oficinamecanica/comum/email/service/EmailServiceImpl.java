package com.grupo51.oficinamecanica.comum.email.service;

import com.grupo51.oficinamecanica.comum.email.config.EmailProperties;
import com.grupo51.oficinamecanica.comum.email.model.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Implementação de EmailService com Thymeleaf
 * Envia emails usando templates HTML
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "spring.mail.host")
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailProperties emailProperties;
    
    public EmailServiceImpl(JavaMailSender mailSender,
                          TemplateEngine templateEngine,
                          EmailProperties emailProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailProperties = emailProperties;
    }
    
    @Override
    public boolean sendEmail(EmailRequest emailRequest) {
        try {
            // Validar dados
            if (emailRequest.getTo() == null || emailRequest.getTo().isEmpty()) {
                log.warn("Tentativa de enviar email sem destinatário");
                return false;
            }
            
            // Processar template Thymeleaf
            String htmlContent = processTemplate(emailRequest);
            
            // Criar mensagem MIME
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Configurar headers
            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(htmlContent, true);
            
            // CC e BCC opcionais
            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                helper.setCc(emailRequest.getCc());
            }
            if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
                helper.setBcc(emailRequest.getBcc());
            }
            
            // Enviar
            mailSender.send(message);
            log.info("Email enviado com sucesso para: {}", emailRequest.getTo());
            return true;
            
        } catch (MessagingException e) {
            log.error("Erro ao enviar email para: {}", emailRequest.getTo(), e);
            return false;
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar email", e);
            return false;
        }
    }
    
    @Override
    public boolean isEmailEnabled() {
        try {
            // Testa se consegue acessar o JavaMailSender
            return mailSender != null;
        } catch (Exception e) {
            log.debug("Email não está habilitado: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Processa o template Thymeleaf com as variáveis fornecidas
     */
    private String processTemplate(EmailRequest emailRequest) {
        Context context = new Context();
        context.setVariables(emailRequest.getVariables());
        
        // Adicionar variáveis globais
        context.setVariable("applicationName", "Oficina Mecânica");
        context.setVariable("supportEmail", emailProperties.getReplyTo());
        context.setVariable("currentYear", java.time.Year.now().getValue());
        
        return templateEngine.process(emailRequest.getTemplateName(), context);
    }
}

