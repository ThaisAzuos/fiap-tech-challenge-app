package com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.events;

/**
 * Envelope padrão de todos os eventos da Saga (publicados e consumidos),
 * conforme contrato definido em docs/arquitetura/eventos/README.md (Dia 1).
 *
 * sagaId == ordemServicoId nesta implementação (simplificação registrada na ADR-009):
 * como cada Saga corresponde a exatamente uma Ordem de Serviço e não há reuso de
 * sagaId entre tentativas, evitamos criar uma entidade/coluna nova só para isso.
 */
public record EventoEnvelope<T>(
        String eventId,
        String eventType,
        String eventVersion,
        String occurredAt,
        String sagaId,
        String ordemServicoId,
        T payload
) {
}
