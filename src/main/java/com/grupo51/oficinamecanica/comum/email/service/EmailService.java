package com.grupo51.oficinamecanica.comum.email.service;

import com.grupo51.oficinamecanica.comum.email.model.EmailRequest;

/**
 * Interface para serviço de email
 * Abstrai a implementação de envio de emails
 */
public interface EmailService {
    
    /**
     * Envia um email baseado em um template Thymeleaf
     * 
     * @param emailRequest Contém destinatário, assunto, template e variáveis
     * @return true se enviado com sucesso, false caso contrário
     */
    boolean sendEmail(EmailRequest emailRequest);
    
    /**
     * Verifica se o serviço de email está disponível
     * 
     * @return true se configurado e disponível
     */
    boolean isEmailEnabled();
}

