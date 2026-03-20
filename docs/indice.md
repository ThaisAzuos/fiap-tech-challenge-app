# Índice de Documentos — Oficina Mecânica API

**Projeto:** Oficina Mecânica API — Tech Challenge  
**Atualizado em:** 20 de Março de 2026

---

## Estrutura da documentação

```
docs/
  ADRs/                     ← Decisões arquiteturais formais
  arquitetura/              ← Diagramas e comparativos
  avaliacao-fase-1/         ← Análise de conformidade da Fase 1
  operacional/              ← Guias de ferramentas e entregáveis
  historico/                ← Documentos de rastreamento histórico
  indice.md                 ← Este arquivo
  leia-primeiro.md          ← Ponto de entrada por perfil de leitor
```

---

## ADRs — Decisões Arquiteturais

| Arquivo | Descrição |
|---------|-----------|
| [`ADRs/ADR-001-autenticacao-jwt.md`](ADRs/ADR-001-autenticacao-jwt.md) | Autenticação e autorização com JWT |
| [`ADRs/ADR-002-clean-architecture.md`](ADRs/ADR-002-clean-architecture.md) | Arquitetura alvo: Clean Architecture/Hexagonal |
| [`ADRs/ADR-003-seguranca-owasp-sonarqube.md`](ADRs/ADR-003-seguranca-owasp-sonarqube.md) | Segurança: OWASP + SonarQube |
| [`ADRs/ADR-004-evolucao-aplicacao.md`](ADRs/ADR-004-evolucao-aplicacao.md) | Evolução da aplicação — Fase 2 |
| [`ADRs/ADRs-fase-1-base-projeto.md`](ADRs/ADRs-fase-1-base-projeto.md) | ADRs base da Fase 1 (retrospectiva) |

---

## Arquitetura

| Arquivo | Descrição |
|---------|-----------|
| [`arquitetura/diagrama-arquitetura.md`](arquitetura/diagrama-arquitetura.md) | Diagrama principal (Mermaid) |
| [`arquitetura/diagrama-arquitetura-simplificado.md`](arquitetura/diagrama-arquitetura-simplificado.md) | Diagrama simplificado (PlantUML) |
| [`arquitetura/comparativo-fase1-fase2.md`](arquitetura/comparativo-fase1-fase2.md) | Comparativo Fase 1 vs Fase 2 |
| [`arquitetura/explicacao-diagrama.md`](arquitetura/explicacao-diagrama.md) | Explicação textual dos diagramas |

---

## Avaliação — Fase 1

| Arquivo | Descrição | Tempo de leitura |
|---------|-----------|-----------------|
| [`avaliacao-fase-1/resposta-direta.md`](avaliacao-fase-1/resposta-direta.md) | Resposta concisa à avaliação | 10 min |
| [`avaliacao-fase-1/avaliacao-detalhada.md`](avaliacao-fase-1/avaliacao-detalhada.md) | Análise técnica profunda (ADRs + OWASP) | 30 min |
| [`avaliacao-fase-1/plano-acao.md`](avaliacao-fase-1/plano-acao.md) | Roadmap de 4 sprints | 25 min |
| [`avaliacao-fase-1/findings-avaliacao.md`](avaliacao-fase-1/findings-avaliacao.md) | Documento formal de achados | 15 min |
| [`avaliacao-fase-1/quadro-resumido.md`](avaliacao-fase-1/quadro-resumido.md) | Tabelas para apresentação | 8 min |

---

## Operacional

| Arquivo | Descrição |
|---------|-----------|
| [`operacional/mailhog-setup.md`](operacional/mailhog-setup.md) | Guia completo do MailHog (SMTP local) |
| [`operacional/sonarqube-producao.md`](operacional/sonarqube-producao.md) | SonarQube em ambiente produção-like |
| [`operacional/entregaveis.md`](operacional/entregaveis.md) | Links dos entregáveis do projeto |

---

## Histórico

| Arquivo | Descrição |
|---------|-----------|
| [`historico/plano-execucao.md`](historico/plano-execucao.md) | Plano de execução de 10 dias |
| [`historico/passo-b-completo.md`](historico/passo-b-completo.md) | Registro da implementação dos templates de e-mail |

---

## Documentos na raiz do projeto

| Arquivo | Descrição |
|---------|-----------|
| [`../README.md`](../README.md) | Guia operacional principal do projeto |
| [`../modules/oficina-clean-mvp/README.md`](../modules/oficina-clean-mvp/README.md) | README do módulo Clean Architecture MVP |
| [`../terraform/README.md`](../terraform/README.md) | README da infraestrutura Terraform |

---

**Índice mantido por:** GitHub Copilot  
**Última revisão:** 20 de Março de 2026
