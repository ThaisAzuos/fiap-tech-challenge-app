package com.grupo37.oficinamecanica.comum.email.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Modelo genérico para envio de emails
 * Suporta templates dinâmicos com variáveis
 */
public class EmailRequest {
    
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String templateName;
    private Map<String, Object> variables;
    
    public EmailRequest() {
        this.variables = new HashMap<>();
    }
    
    public EmailRequest(String to, String subject, String templateName) {
        this();
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
    }
    
    // Getters e Setters
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getCc() {
        return cc;
    }
    
    public void setCc(String cc) {
        this.cc = cc;
    }
    
    public String getBcc() {
        return bcc;
    }
    
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
    
    public void addVariable(String key, Object value) {
        this.variables.put(key, value);
    }
}

