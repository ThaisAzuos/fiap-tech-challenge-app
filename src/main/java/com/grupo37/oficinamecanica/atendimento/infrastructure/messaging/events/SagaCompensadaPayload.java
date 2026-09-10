package com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.events;

import java.util.List;

/**
 * Payload do evento SagaCompensada (routing key saga.compensada), publicado
 * pelo orquestrador (OS Service) quando uma etapa da Saga falha e a OS
 * precisa ser cancelada.
 */
public record SagaCompensadaPayload(
        String etapaFalha,
        String motivoCompensacao,
        List<String> acoesCompensatorias
) {
}
