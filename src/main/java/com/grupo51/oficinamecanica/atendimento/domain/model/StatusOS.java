package com.grupo51.oficinamecanica.atendimento.domain.model;

/**
 * Enum de status para Ordem de Serviço
 * 
 * Fluxos válidos:
 * 1. Normal: RECEBIDA → EM_DIAGNOSTICO → AGUARDANDO_APROVACAO → EM_EXECUCAO → FINALIZADA → ENTREGUE
 * 2. Cancelamento: Qualquer status → CANCELADA (terminal)
 * 3. Rejeição: AGUARDANDO_APROVACAO → EM_DIAGNOSTICO (retrocesso permitido apenas neste caso)
 */
public enum StatusOS {
    RECEBIDA,
    EM_DIAGNOSTICO,
    AGUARDANDO_APROVACAO,
    EM_EXECUCAO,
    FINALIZADA,
    ENTREGUE,
    CANCELADA  // Novo status terminal
}
