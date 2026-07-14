package com.grupo37.oficinamecanica.atendimento.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo37.oficinamecanica.atendimento.domain.model.ItemOS;
import com.grupo37.oficinamecanica.atendimento.domain.model.ItemServicoOS;
import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.events.EventoEnvelope;
import com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.events.OrdemServicoCriadaPayload;
import com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.events.SagaCompensadaPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Publica os eventos da Saga originados no OS Service (OrdemServicoCriada e
 * SagaCompensada). Serialização manual em JSON puro (não usa o conversor
 * padrão do Spring AMQP com cabeçalho __TypeId__), porque cada serviço da
 * Saga é um código-fonte/pacote Java diferente e o contrato precisa
 * funcionar entre serviços — ver ADR-009.
 *
 * Fail-safe: qualquer erro ao publicar é apenas logado (não interrompe o
 * fluxo principal de abertura/atualização da OS, que já foi persistido).
 * Também não publica nada quando saga.messaging.enabled=false (testes).
 */
@Slf4j
@Component
public class OrdemServicoEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final boolean messagingEnabled;
    private final String exchangeName;

    public OrdemServicoEventPublisher(RabbitTemplate rabbitTemplate,
                                       ObjectMapper objectMapper,
                                       @Value("${saga.messaging.enabled:true}") boolean messagingEnabled,
                                       @Value("${saga.messaging.exchange:tech-challenge.saga}") String exchangeName) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.messagingEnabled = messagingEnabled;
        this.exchangeName = exchangeName;
    }

    public void publicarOrdemServicoCriada(OrdemServico os) {
        if (!messagingEnabled) {
            return;
        }
        try {
            List<OrdemServicoCriadaPayload.ItemPrevisto> itensPrevistos = new java.util.ArrayList<>();
            for (ItemOS item : os.getItens()) {
                itensPrevistos.add(new OrdemServicoCriadaPayload.ItemPrevisto(item.getNomePecaSnapshot(), "PECA"));
            }
            for (ItemServicoOS servico : os.getServicos()) {
                itensPrevistos.add(new OrdemServicoCriadaPayload.ItemPrevisto(servico.getNomeServicoSnapshot(), "SERVICO"));
            }

            OrdemServicoCriadaPayload payload = new OrdemServicoCriadaPayload(
                    os.getVeiculo().getDono().getCpf(),
                    os.getVeiculo().getDono().getNome(),
                    os.getVeiculo().getDono().getEmail(),
                    os.getVeiculo().getPlaca(),
                    itensPrevistos,
                    os.getValorTotal(),
                    os.getStatus().name()
            );

            publicar("OrdemServicoCriada", "os.criada", os.getId(), payload);
        } catch (Exception e) {
            log.error("Falha ao publicar evento OrdemServicoCriada para a OS {}", os.getId(), e);
        }
    }

    public void publicarSagaCompensada(UUID ordemServicoId, String etapaFalha, String motivo, List<String> acoesCompensatorias) {
        if (!messagingEnabled) {
            return;
        }
        try {
            SagaCompensadaPayload payload = new SagaCompensadaPayload(etapaFalha, motivo, acoesCompensatorias);
            publicar("SagaCompensada", "saga.compensada", ordemServicoId, payload);
        } catch (Exception e) {
            log.error("Falha ao publicar evento SagaCompensada para a OS {}", ordemServicoId, e);
        }
    }

    private <T> void publicar(String eventType, String routingKey, UUID ordemServicoId, T payload) throws Exception {
        String sagaId = ordemServicoId.toString(); // sagaId == ordemServicoId (ADR-009)

        EventoEnvelope<T> envelope = new EventoEnvelope<>(
                UUID.randomUUID().toString(),
                eventType,
                "1.0",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                sagaId,
                ordemServicoId.toString(),
                payload
        );

        String json = objectMapper.writeValueAsString(envelope);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, json);
        log.info("Evento {} publicado para a OS {} (routing key {})", eventType, ordemServicoId, routingKey);
    }
}
