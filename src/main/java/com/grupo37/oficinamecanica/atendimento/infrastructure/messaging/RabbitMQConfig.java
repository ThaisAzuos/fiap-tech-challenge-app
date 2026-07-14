package com.grupo37.oficinamecanica.atendimento.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia RabbitMQ da Saga (exchange, filas e bindings), conforme desenhado
 * em docs/arquitetura/fase4-visao-geral.md (Dia 1).
 *
 * IMPORTANTE (protege os testes @SpringBootTest existentes): o RabbitAdmin
 * autoconfigurado pelo Spring Boot declara todos os beans Exchange/Queue/Binding
 * do contexto no broker assim que o contexto sobe (ContextRefreshedEvent) — ou
 * seja, mesmo sem nenhum @RabbitListener, apenas ter esses beans registrados já
 * dispara uma tentativa de conexão real ao RabbitMQ. Por isso esta classe inteira
 * é desativada quando saga.messaging.enabled=false (ver src/test/resources/
 * application.yml), com matchIfMissing=true para manter o comportamento padrão
 * ativado em ambientes reais. Ver ADR-009.
 */
@Configuration
@ConditionalOnProperty(prefix = "saga.messaging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    @Value("${saga.messaging.exchange:tech-challenge.saga}")
    private String exchangeName;

    @Value("${saga.messaging.queue:os-service.eventos-saga}")
    private String queueName;

    @Value("${saga.messaging.dlq:os-service.eventos-saga.dlq}")
    private String dlqName;

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue sagaDlq() {
        return QueueBuilder.durable(dlqName).build();
    }

    @Bean
    public Queue osServiceSagaQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", dlqName)
                .build();
    }

    @Bean
    public Binding bindingOrcamento(Queue osServiceSagaQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(osServiceSagaQueue).to(sagaExchange).with("orcamento.*");
    }

    @Bean
    public Binding bindingPagamento(Queue osServiceSagaQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(osServiceSagaQueue).to(sagaExchange).with("pagamento.*");
    }

    @Bean
    public Binding bindingExecucao(Queue osServiceSagaQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(osServiceSagaQueue).to(sagaExchange).with("execucao.*");
    }
}
