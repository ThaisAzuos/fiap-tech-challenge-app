# Architecture Decision Records (ADRs)

## ADR-001: Escolha da Nuvem
- **Contexto**: Necessidade de serviços serverless, banco gerenciado e integração com observabilidade.
- **Decisão**: Utilizar AWS como provedor de nuvem.
- **Consequências**:
  - Integração nativa com Lambda, API Gateway e RDS.
  - Custos previsíveis e escalabilidade.
  - Dependência da AWS para serviços críticos.

## ADR-002: Banco de Dados
- **Contexto**: Necessidade de consistência relacional e suporte a dados semi-estruturados.
- **Decisão**: PostgreSQL gerenciado (AWS RDS).
- **Consequências**:
  - Suporte a JSON.
  - Ferramentas maduras de replicação e backup.
  - Flexibilidade para consultas complexas.

## ADR-003: Autenticação
- **Contexto**: Autenticação via CPF e proteção de APIs.
- **Decisão**: JWT gerado por Lambda após validação de CPF.
- **Consequências**:
  - Segurança reforçada.
  - Escalabilidade com funções serverless.
  - Necessidade de monitoramento de tokens.
