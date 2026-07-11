# Contratos de Eventos — Saga da Ordem de Serviço (Fase 4)

Todos os eventos trafegam pelo exchange `tech-challenge.saga` (tipo *topic*) no RabbitMQ. Cada evento é um JSON com um envelope comum e um `payload` específico. Os schemas completos (JSON Schema draft-07) estão nos arquivos `.schema.json` deste diretório.

## Envelope comum

```json
{
  "eventId": "uuid",
  "eventType": "OrdemServicoCriada",
  "eventVersion": "1.0",
  "occurredAt": "2026-07-10T14:32:00Z",
  "sagaId": "uuid — correlaciona todos os eventos de uma mesma Saga",
  "ordemServicoId": "uuid",
  "payload": { "...": "específico de cada evento" }
}
```

## Catálogo de eventos

| Evento | Routing key | Publicado por | Consumido por | Schema |
|---|---|---|---|---|
| OrdemServicoCriada | `os.criada` | OS Service | Billing Service | [ordem-servico-criada.schema.json](./ordem-servico-criada.schema.json) |
| OrcamentoGerado | `orcamento.gerado` | Billing Service | OS Service | [orcamento-gerado.schema.json](./orcamento-gerado.schema.json) |
| OrcamentoAprovado | `orcamento.aprovado` | Billing Service | OS Service | [orcamento-aprovado.schema.json](./orcamento-aprovado.schema.json) |
| OrcamentoReprovado | `orcamento.reprovado` | Billing Service | OS Service | [orcamento-reprovado.schema.json](./orcamento-reprovado.schema.json) |
| PagamentoConfirmado | `pagamento.confirmado` | Billing Service | OS Service, Execution Service | [pagamento-confirmado.schema.json](./pagamento-confirmado.schema.json) |
| PagamentoFalhou | `pagamento.falhou` | Billing Service | OS Service | [pagamento-falhou.schema.json](./pagamento-falhou.schema.json) |
| ExecucaoConcluida | `execucao.concluida` | Execution Service | OS Service | [execucao-concluida.schema.json](./execucao-concluida.schema.json) |
| ExecucaoCancelada | `execucao.cancelada` | Execution Service | OS Service | [execucao-cancelada.schema.json](./execucao-cancelada.schema.json) |
| SagaCompensada | `saga.compensada` | OS Service (orquestrador) | Billing Service, Execution Service | [saga-compensada.schema.json](./saga-compensada.schema.json) |

## Convenções

- **Idempotência**: todo consumidor deve tratar reentrega de mensagem (deduplicar por `eventId`).
- **Dead-letter queue**: mensagens rejeitadas ou que estourarem tentativas vão para `<fila>.dlq`.
- **Versionamento**: mudanças incompatíveis no payload incrementam `eventVersion` e exigem atualização deste índice.
- **Correlação da Saga**: `sagaId` é obrigatório em todos os eventos e deve ser o mesmo para todos os eventos de uma mesma OS, do início ao fim (ou até a compensação).

> **Atualização (Dia 3 / [ADR-009](../ADRs/ADR-009-refatoracao-os-service-saga.md))**: o schema `OrdemServicoCriada` foi corrigido durante a implementação — o domínio `Cliente`/`Veiculo` do OS Service não possui id UUID próprio (usa CPF e placa como chaves naturais), então o payload usa `clienteCpf`/`veiculoPlaca` em vez de `clienteId`/`veiculoId`, e `status` reflete o `StatusOS` real da OS (não um valor fixo).

Ver também o fluxo completo em [`../fase4-visao-geral.md`](../fase4-visao-geral.md) e a decisão de mensageria em [`../ADRs/ADR-006-mensageria-rabbitmq.md`](../ADRs/ADR-006-mensageria-rabbitmq.md).
