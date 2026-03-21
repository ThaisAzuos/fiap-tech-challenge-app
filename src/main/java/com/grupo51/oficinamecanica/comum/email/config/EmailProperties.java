package com.grupo51.oficinamecanica.comum.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuração de Email para aplicação
 * Pode ser customizada em application.yml
 */
@Component
@ConfigurationProperties(prefix = "app.email")
public class EmailProperties {
    
    private String from = "noreply@oficinamecanica.com";
    private String fromName = "Oficina Mecânica";
    private String replyTo = "suporte@oficinamecanica.com";
    
    // Getters e Setters
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getFromName() {
        return fromName;
    }
    
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    
    public String getReplyTo() {
        return replyTo;
    }
    
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
}

