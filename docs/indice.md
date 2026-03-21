# Índice de Documentos — Oficina Mecânica API

**Projeto:** Oficina Mecânica API — Tech Challenge  
**Atualizado em:** 21 de Março de 2026

## Estrutura da documentação

```
docs/
  ADRs/                     ← Decisões arquiteturais formais
  arquitetura/              ← Diagramas e comparativos
  operacional/              ← Guias de operação (MailHog e SonarQube)
  historico/                ← Registros históricos de execução
  indice.md                 ← Este arquivo
  leia-primeiro.md          ← Ponto de entrada objetivo
```

## ADRs — Decisões Arquiteturais

| Arquivo | Descrição |
|---------|-----------|
| [`ADRs/ADR-001-autenticacao-jwt.md`](ADRs/ADR-001-autenticacao-jwt.md) | Autenticação e autorização com JWT |
| [`ADRs/ADR-002-clean-architecture.md`](ADRs/ADR-002-clean-architecture.md) | Arquitetura alvo: Clean Architecture/Hexagonal |
| [`ADRs/ADR-003-seguranca-owasp-sonarqube.md`](ADRs/ADR-003-seguranca-owasp-sonarqube.md) | Segurança: OWASP + SonarQube |
| [`ADRs/ADR-004-evolucao-aplicacao.md`](ADRs/ADR-004-evolucao-aplicacao.md) | Evolução da aplicação e entregas da Fase 2 |

## Arquitetura

| Arquivo | Descrição |
|---------|-----------|
| [`arquitetura/diagrama-arquitetura.md`](arquitetura/diagrama-arquitetura.md) | Diagrama principal (Mermaid) |
| [`arquitetura/diagrama-arquitetura-simplificado.md`](arquitetura/diagrama-arquitetura-simplificado.md) | Diagrama simplificado (PlantUML) |
| [`arquitetura/comparativo-fase1-fase2.md`](arquitetura/comparativo-fase1-fase2.md) | Comparativo visual Fase 1 vs Fase 2 |

## Operacional

| Arquivo | Descrição |
|---------|-----------|
| [`operacional/mailhog-setup.md`](operacional/mailhog-setup.md) | Guia de setup e testes de e-mail com MailHog |
| [`operacional/sonarqube-producao.md`](operacional/sonarqube-producao.md) | SonarQube em ambiente production-like |

## Histórico

| Arquivo | Descrição |
|---------|-----------|
| [`historico/plano-execucao.md`](historico/plano-execucao.md) | Plano de execução de 10 dias |
| [`historico/passo-b-completo.md`](historico/passo-b-completo.md) | Registro da implementação dos templates de e-mail |

## Documentos na raiz do projeto

| Arquivo | Descrição |
|---------|-----------|
| [`../README.md`](../README.md) | Guia operacional principal do projeto |
| [`../modules/oficina-clean-mvp/README.md`](../modules/oficina-clean-mvp/README.md) | README do módulo Clean Architecture MVP |
| [`../terraform/README.md`](../terraform/README.md) | README da infraestrutura Terraform |

## Nota de higienização

Foram removidos documentos redundantes, obsoletos ou com dados desatualizados da avaliação inicial da Fase 1, mantendo apenas conteúdo aderente ao estado atual do projeto.
