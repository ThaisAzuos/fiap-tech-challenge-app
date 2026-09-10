package com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.events;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Forma "genérica" de um evento recebido de outro serviço (Billing/Execution).
 * O payload é mantido como JsonNode e só é interpretado depois que sabemos,
 * pela routing key / eventType, qual evento é — evitamos assim depender do
 * cabeçalho __TypeId__ do Jackson2JsonMessageConverter (que exige a mesma
 * classe Java em produtor e consumidor; aqui cada serviço é um código-fonte
 * separado). Ver ADR-009.
 */
public record EventoSagaRecebido(
        String eventId,
        String eventType,
        String eventVersion,
        String occurredAt,
        String sagaId,
        String ordemServicoId,
        JsonNode payload
) {
    public String payloadText(String campo) {
        if (payload == null || !payload.hasNonNull(campo)) {
            return null;
        }
        return payload.get(campo).asText();
    }
}
