package com.grupo37.oficinamecanica.atendimento.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo37.oficinamecanica.atendimento.application.usecase.SagaOrchestratorService;
import com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.events.EventoSagaRecebido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Consumidor único da fila da Saga do OS Service (os-service.eventos-saga),
 * despachando por routing key para o SagaOrchestratorService.
 *
 * Desativado quando saga.messaging.enabled=false: um bean @RabbitListener é
 * um container ATIVO — diferente do RabbitTemplate (passivo/preguiçoso), ele
 * tenta se conectar e começar a consumir assim que o contexto sobe. Por isso
 * fica sob a mesma condição que a RabbitMQConfig, para não quebrar os testes
 * @SpringBootTest existentes que não têm um broker disponível. Ver ADR-009.
 *
 * Deserialização manual (sem depender do cabeçalho __TypeId__ do Jackson):
 * cada serviço da Saga é um código-fonte Java separado, então o payload é
 * lido primeiro como JSON genérico e só então mapeado, usando a routing key
 * (via eventType) para decidir o que fazer.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "saga.messaging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SagaEventListener {

    private final ObjectMapper objectMapper;
    private final SagaOrchestratorService sagaOrchestratorService;

    public SagaEventListener(ObjectMapper objectMapper, SagaOrchestratorService sagaOrchestratorService) {
        this.objectMapper = objectMapper;
        this.sagaOrchestratorService = sagaOrchestratorService;
    }

    @RabbitListener(queues = "${saga.messaging.queue:os-service.eventos-saga}")
    public void receber(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            EventoSagaRecebido evento = objectMapper.readValue(body, EventoSagaRecebido.class);
            UUID ordemServicoId = UUID.fromString(evento.ordemServicoId());

            log.info("Evento {} recebido para a OS {} (routing key {})", evento.eventType(), ordemServicoId, routingKey);

            switch (routingKey) {
                case "orcamento.aprovado" -> sagaOrchestratorService.tratarOrcamentoAprovado(ordemServicoId);
                case "orcamento.reprovado" -> sagaOrchestratorService.tratarOrcamentoReprovado(
                        ordemServicoId, evento.payloadText("motivo"));
                case "pagamento.confirmado" -> sagaOrchestratorService.tratarPagamentoConfirmado(ordemServicoId);
                case "pagamento.falhou" -> sagaOrchestratorService.tratarPagamentoFalhou(
                        ordemServicoId, evento.payloadText("motivo"));
                case "execucao.concluida" -> sagaOrchestratorService.tratarExecucaoConcluida(ordemServicoId);
                default -> log.warn("Routing key não tratada pelo OS Service: {} (evento {})", routingKey, evento.eventType());
            }
        } catch (Exception e) {
            log.error("Falha ao processar evento da Saga (routing key {}). Encaminhando para a DLQ. Corpo: {}",
                    routingKey, body, e);
            throw new AmqpRejectAndDontRequeueException("Falha ao processar evento da Saga: " + routingKey, e);
        }
    }
}
