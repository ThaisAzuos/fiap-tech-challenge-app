# ADR 006 — Mensageria Assíncrona com RabbitMQ

**Status:** Aceita
**Data:** 2026-07-10
**Fase:** Tech Challenge Fase 4 — FIAP SOAT

---

## Contexto

O desafio exige que a comunicação entre microsserviços combine, quando necessário, chamadas síncronas (REST) com mensageria assíncrona para eventos e integração desacoplada, sem que nenhum serviço acesse diretamente o banco de outro. Essa mensageria também é a espinha dorsal da Saga (ver ADR-007): cada etapa do fluxo (OS criada, orçamento gerado/aprovado/reprovado, pagamento confirmado/falho, execução concluída/cancelada) precisa ser propagada de forma confiável entre OS Service, Billing Service e Execution Service.

## Decisão

Adotar **RabbitMQ** como broker de mensageria, provisionado via Helm (chart Bitnami) no mesmo cluster EKS já existente (`fiap-tech-challenge-k8s-terraform`), com:

- Um **exchange do tipo topic**: `tech-challenge.saga`.
- Routing keys por evento, no padrão `<contexto>.<evento>` (ex.: `os.criada`, `orcamento.aprovado`, `pagamento.confirmado`, `execucao.concluida`).
- Uma fila por serviço consumidor, vinculada às routing keys relevantes (ex.: `billing-service.os-criada`, `execution-service.pagamento-confirmado`, `os-service.eventos-saga`).
- Contratos de evento versionados como JSON Schema, documentados em `docs/arquitetura/eventos/` deste repositório (fonte única de verdade, referenciada pelos demais repositórios).

## Alternativas consideradas

- **Apache Kafka**: mais robusto para altíssimo volume e replay de eventos, porém mais complexo de operar e configurar em Kubernetes dentro do prazo de uma semana, com curva de aprendizado maior para operação solo.
- **AWS SQS/SNS**: manteria tudo dentro do ecossistema AWS já usado na Fase 3, mas adiciona dependência de infraestrutura gerenciada adicional (filas, tópicos, IAM, custos) fora do cluster, aumentando a superfície de configuração no prazo curto.
- **Somente REST síncrono entre serviços**: rejeitada; não atende ao requisito de mensageria assíncrona e acopla a disponibilidade dos serviços entre si, dificultando o rollback da Saga em caso de falha.

RabbitMQ foi escolhido por ser mais simples de subir e operar sozinho em uma semana, ter suporte maduro no Spring Boot (`spring-boot-starter-amqp`) e um plugin de management com interface visual útil para depurar filas durante o desenvolvimento e a gravação do vídeo de demonstração.

## Consequências

- Cada serviço publica e consome eventos via `spring-boot-starter-amqp`, sem acoplamento direto a outro serviço.
- É necessário garantir idempotência no consumo (mensagens podem ser reentregues) e política de dead-letter queue para mensagens que falharem repetidamente.
- A infraestrutura do RabbitMQ (Helm release, namespace, credenciais) é responsabilidade do `fiap-tech-challenge-k8s-terraform` (ver ADR correspondente nesse repositório).
- O contrato de cada evento vive versionado neste repositório e deve ser atualizado em conjunto por quem alterar publishers/consumers.
