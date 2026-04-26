package com.grupo37.oficinamecanica.agendamento.model;

/**
 * Define a finalidade do compromisso na oficina.
 * Conforme o dicionário de domínio: Análise ou Execução.
 */
public enum TipoAgendamento {

    /**
     * Reservado para o Diagnóstico Técnico inicial realizado pelo Mecânico.
     */
    ANALISE,

    /**
     * Reservado para a realização do Serviço Corretivo (uso de Box/Elevador).
     */
    EXECUCAO
}
