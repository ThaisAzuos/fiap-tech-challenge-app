# ADR 007 — Saga Pattern Orquestrada para o Fluxo de Ordem de Serviço

**Status:** Aceita
**Data:** 2026-07-10
**Fase:** Tech Challenge Fase 4 — FIAP SOAT

---

## Contexto

O fluxo de negócio (abertura de OS → geração de orçamento → aprovação → pagamento → execução → conclusão) atravessa três microsserviços com bancos de dados independentes. O desafio exige consistência entre essas etapas, com rollback/compensação seguro em caso de falha em qualquer ponto, aplicando o Saga Pattern — orquestrado (com orquestrador central) ou coreografado (via eventos, sem orquestrador central).

## Decisão

Adotar **Saga orquestrada**, com o orquestrador residindo no **OS Service** (por ser o serviço que inicia o fluxo de negócio ao abrir a OS).

Fluxo feliz:

1. OS Service cria a OS em `AGUARDANDO_ORCAMENTO` e publica `OrdemServicoCriada`.
2. Billing Service gera o orçamento e publica `OrcamentoGerado`.
3. Cliente aprova (endpoint no Billing Service) → publica `OrcamentoAprovado`.
4. Billing Service processa o pagamento via Mercado Pago e publica `PagamentoConfirmado`.
5. Execution Service assume a execução e publica `ExecucaoConcluida` ao final.
6. O orquestrador no OS Service escuta cada evento acima e avança o status da OS.

Fluxo de falha/compensação:

- Se o orçamento for reprovado (`OrcamentoReprovado`), o pagamento falhar (`PagamentoFalhou`) ou a execução for cancelada (`ExecucaoCancelada`), o orquestrador no OS Service cancela a OS e publica `SagaCompensada`, sinalizando aos demais serviços que qualquer efeito colateral já processado (ex.: pré-reserva de orçamento) deve ser estornado/anulado.

## Alternativas consideradas

- **Saga coreografada** (cada serviço reage a eventos dos outros, sem orquestrador central): mais desacoplada, porém mais trabalhosa de implementar e testar dentro do prazo de uma semana — a lógica de "quem compensa o quê" fica espalhada entre serviços, dificultando depuração e documentação rápida.
- **Transação distribuída com 2PC (two-phase commit)**: rejeitada; não é compatível com bancos de dados heterogêneos (PostgreSQL + MongoDB) nem com a independência de implantação exigida para microsserviços.

A orquestração foi escolhida por concentrar a lógica do fluxo e da compensação em um único lugar (o OS Service), o que facilita testar cada cenário de falha, documentar a decisão e demonstrá-la no vídeo final dentro do prazo.

## Consequências

- O OS Service ganha uma responsabilidade adicional (orquestração), reforçando seu papel como "hub" do domínio.
- Cada serviço participante precisa expor endpoints ou consumidores de compensação (ex.: estornar orçamento, cancelar execução).
- É necessário correlacionar eventos de uma mesma Saga via um identificador comum (`sagaId`/`correlationId`), presente no envelope de todos os eventos (ver contratos em `docs/arquitetura/eventos/`).
- O cenário de falha com rollback deve ser coberto por pelo menos um teste BDD (ver plano de testes do Dia 6) e demonstrado ao vivo no vídeo de entrega.
