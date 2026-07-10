# ADR 005 — Decomposição do Monólito em Arquitetura de Microsserviços

**Status:** Aceita
**Data:** 2026-07-10
**Fase:** Tech Challenge Fase 4 — FIAP SOAT

---

## Contexto

Na Fase 3, o sistema da oficina mecânica era um monólito Spring Boot único (`fiap-tech-challenge-app`) responsável por cadastro, agendamento, atendimento (ordens de serviço) e regras que hoje envolvem orçamento/pagamento, além de consumir o `fiap-tech-challenge-lambda-auth` para autenticação.

Com a oficina atingindo escala nacional e múltiplas filiais, o desafio da Fase 4 exige:

- Separar responsabilidades em, no mínimo, 3 microsserviços independentes, cada um com repositório, infraestrutura e banco de dados próprios.
- Garantir que nenhum serviço acesse diretamente o banco de outro.
- Coordenar transações críticas e distribuídas (abertura de OS, orçamento, aprovação, pagamento e execução) com consistência via Saga Pattern.

## Decisão

Decompor o domínio em três microsserviços de negócio, reaproveitando o máximo possível do código e da infraestrutura já existentes da Fase 3:

1. **OS Service** (`fiap-tech-challenge-app`, reaproveitado e reduzido de escopo)
   - Mantém: cadastro (cliente/veículo/funcionário), agendamento, abertura/consulta/histórico de OS.
   - Remove do domínio: regras de orçamento e pagamento (migradas para o Billing Service) e regras de fila/execução (migradas para o Execution Service).
   - Passa a hospedar o orquestrador da Saga, por ser o serviço que inicia o fluxo de negócio.

2. **Billing Service** (`fiap-tech-challenge-billing-service`, novo repositório)
   - Orçamento, aprovação/reprovação e integração de pagamento com Mercado Pago.

3. **Execution Service** (`fiap-tech-challenge-execution-service`, novo repositório)
   - Fila de execução, diagnóstico, reparo e conclusão.

O `fiap-tech-challenge-lambda-auth` é mantido como serviço de apoio (Auth Service), consumido pelos três serviços de negócio para validação de JWT. Os repositórios `fiap-tech-challenge-k8s-terraform` e `fiap-tech-challenge-db-terraform` passam a representar a infraestrutura compartilhada de cluster e de dados do OS Service, respectivamente — cada novo microsserviço traz seu próprio banco e seus próprios manifestos Kubernetes dentro do seu repositório.

## Alternativas consideradas

- **Manter o monólito e apenas modularizar em pacotes internos**: rejeitada, pois não atende ao requisito obrigatório de repositórios, infraestrutura e bancos de dados independentes por serviço.
- **Dividir em mais de 3 serviços (ex.: separar cadastro do OS Service)**: rejeitada por ora devido ao prazo de uma semana; o mínimo de 3 serviços de negócio bem separados e funcionando é priorizado sobre uma granularidade maior.
- **Reescrever tudo do zero em um novo repositório único "monorepo" de microsserviços**: rejeitada; reaproveitar o `fiap-tech-challenge-app` como OS Service reduz risco e retrabalho dentro do prazo.

## Consequências

- Cada serviço pode ser desenvolvido, testado, implantado e escalado de forma independente.
- Passa a existir comunicação assíncrona entre serviços (ver ADR-006) e um fluxo transacional coordenado por Saga (ver ADR-007).
- O `fiap-tech-challenge-app` perde escopo (deixa de tratar orçamento/pagamento/execução), o que exige remover/isolar esse código e atualizar testes e documentação.
- Aumenta a complexidade operacional (mais repositórios, pipelines e serviços para monitorar), compensada pela independência de deploy e pela aderência aos requisitos obrigatórios da Fase 4.
