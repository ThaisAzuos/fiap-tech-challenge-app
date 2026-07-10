# ADR 008 — Banco de Dados Próprio por Microsserviço (PostgreSQL + MongoDB)

**Status:** Aceita
**Data:** 2026-07-10
**Fase:** Tech Challenge Fase 4 — FIAP SOAT

---

## Contexto

O desafio exige que cada microsserviço tenha seu próprio banco de dados, sem acesso direto ao banco de outro serviço, e que o sistema utilize ao menos um banco relacional (SQL) e ao menos um banco não relacional (NoSQL).

## Decisão

- **OS Service**: continua usando o **PostgreSQL** já provisionado via RDS (`fiap-tech-challenge-db-terraform`), agora de uso **exclusivo** deste serviço. Cobre o requisito de banco relacional.
- **Billing Service**: usa **MongoDB** (instância própria, via Helm/Bitnami no cluster), adequado para os documentos de orçamento e registro de pagamento (payloads semiestruturados, incluindo retorno de webhooks do Mercado Pago).
- **Execution Service**: usa **MongoDB** (instância própria e isolada da do Billing Service, via Helm/Bitnami no cluster), para o estado da fila de execução e histórico de diagnóstico/reparo.

Nenhum serviço acessa o banco de outro diretamente; toda troca de informação entre serviços ocorre via eventos (RabbitMQ, ADR-006) ou APIs REST síncronas expostas propositalmente.

## Alternativas consideradas

- **Uma instância RDS PostgreSQL por serviço**: rejeitada para Billing e Execution neste momento, por ser mais lenta de provisionar (Terraform + espera de disponibilidade do RDS) dentro do prazo de uma semana, e por não ser necessária — o requisito pede pelo menos um SQL e um NoSQL no sistema como um todo, não um SQL por serviço.
- **MongoDB Atlas (gerenciado, fora do cluster)**: rejeitado por adicionar uma conta/billing externo e uma nova credencial a gerenciar sob prazo apertado; rodar MongoDB via Helm no mesmo cluster EKS já existente é mais rápido de configurar e não introduz novo provedor.
- **Um único MongoDB compartilhado entre Billing e Execution**: rejeitada; violaria o requisito de banco próprio por serviço e criaria acoplamento indevido entre os dois domínios.

## Consequências

- O `fiap-tech-challenge-db-terraform` deixa de representar "o banco da aplicação" e passa a representar especificamente a infraestrutura de dados do OS Service.
- As duas instâncias de MongoDB (Billing e Execution) são provisionadas como parte da infraestrutura do próprio `k8s-terraform` (Helm releases), mas com isolamento de namespace, credenciais e PVC entre si e em relação ao PostgreSQL.
- Cada novo serviço deve documentar seu modelo de dados e a justificativa da escolha de banco em seu próprio README (ver checklist de manuais do plano de execução).
