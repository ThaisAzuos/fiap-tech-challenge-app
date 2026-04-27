# Plano Executável - Tech Challenge Fase 3
## Separação de Repositórios + Serverless + Monitoramento Corporativo

**Versão**: 1.0  
**Data**: 2026-04-27  
**Duração Total**: 12 semanas (5 subfases)  
**Status**: 🔵 Planejamento Aprovado

---

## 📋 Visão Geral

A Fase 3 transforma a aplicação de uma arquitetura monolítica em uma solução empresarial serverless com:
- **4 repositórios separados** com CI/CD independente
- **Lambda Serverless** para autenticação com CPF
- **API Gateway** para roteamento e proteção
- **Observabilidade corporativa** (Datadog/New Relic)
- **Documentação arquitetural completa** (ADRs, RFCs, Diagramas ER/Sequência)

---

## 🗓️ Subfases e Timeline

| Fase | Semanas | Objetivo | Entregáveis |
|------|---------|----------|------------|
| **3a** | 1-2 | Planejamento & Análise | ADRs, RFCs, Diagramas ER/Sequência, Plano Migração |
| **3b** | 3-5 | Setup 4 Repositórios | Repos criados, CI/CD base, branch protection |
| **3c** | 6-8 | Lambda + API Gateway | Autenticação serverless funcional |
| **3d** | 9-10 | Observabilidade | Datadog/New Relic, dashboards, alertas |
| **3e** | 11-12 | Documentação + Vídeo | PDF final, vídeo 15min, entrega completa |

---

## 🔴 FASE 3a: Preparação e Análise (Semanas 1-2)

### Tarefas Sequenciadas

| ID | Tarefa | Descrição | Duração | Status |
|----|--------|-----------|---------|--------|
| 3a.1 | Completar ADRs | Nuvem, BD, Auth, CI/CD, Observabilidade, Versionamento | 2 dias | ⏳ Pendente |
| 3a.2 | Documentar RFCs | Branch protection, deploy strategy, monitoramento SLO | 1 dia | ⏳ Pendente |
| 3a.3 | Criar Diagrama ER | Visão relacional + relacionamentos | 1 dia | ⏳ Pendente |
| 3a.4 | Criar Diagramas de Sequência | Auth Lambda, abertura OS, deploy K8s | 1 dia | ⏳ Pendente |
| 3a.5 | Definir estrutura repos | Branches, secrets, workflows | 1 dia | ⏳ Pendente |
| 3a.6 | Mapear código para 4 repos | Módulos → repos, dependências internas | 1 dia | ⏳ Pendente |
| 3a.7 | Documentar plano migração | Checklist, rollback strategy | 1 dia | ⏳ Pendente |

### Entregáveis Esperados
- ✅ [docs/Fase03/ADRs.md] - 8 ADRs documentadas
- ✅ [docs/Fase03/RFCs.md] - 5 RFCs documentadas
- ✅ [docs/Fase03/DiagramaER.md] - Mermaid + descrição
- ✅ [docs/Fase03/DiagramasSequencia.md] - 3+ diagramas
- ✅ [docs/Fase03/EstruturaN4Repos.md] - Layout dos repos
- ✅ [docs/Fase03/PlanoMigracao.md] - Estratégia de migração

---

## 🟠 FASE 3b: Setup dos 4 Repositórios (Semanas 3-5)

### Repositório 1: Lambda Serverless (fiap-tech-challenge-lambda-auth)

| ID | Tarefa | Duração | Teste | Status |
|----|--------|---------|-------|--------|
| 3b.1.1 | Criar repo GitHub | 0.5 dia | — | ⏳ |
| 3b.1.2 | Node.js/Python runtime | 3 dias | Unit tests | ⏳ |
| 3b.1.3 | Dockerfile multi-stage | 1 dia | Build test | ⏳ |
| 3b.1.4 | GitHub Actions workflow | 2 dias | Dry-run | ⏳ |
| 3b.1.5 | Secrets management | 1 dia | Verify | ⏳ |
| 3b.1.6 | Branch protection | 0.5 dia | Config test | ⏳ |
| 3b.1.7 | README | 1 dia | Review | ⏳ |

### Repositório 2: Infraestrutura Kubernetes (fiap-tech-challenge-k8s-terraform)

| ID | Tarefa | Duração | Teste | Status |
|----|--------|---------|-------|--------|
| 3b.2.1 | Criar repo GitHub | 0.5 dia | — | ⏳ |
| 3b.2.2 | Terraform: VPC, EKS, HPA | 3 dias | terraform plan | ⏳ |
| 3b.2.3 | Outputs exportáveis | 1 dia | terraform output | ⏳ |
| 3b.2.4 | Documentar variáveis | 1 dia | Review | ⏳ |
| 3b.2.5 | GitHub Actions CI/CD | 2 dias | Dry-run | ⏳ |
| 3b.2.6 | Secrets & AWS credentials | 1 dia | Verify | ⏳ |
| 3b.2.7 | Branch protection | 0.5 dia | Config | ⏳ |
| 3b.2.8 | README | 1 dia | Review | ⏳ |

### Repositório 3: Banco de Dados (fiap-tech-challenge-db-terraform)

| ID | Tarefa | Duração | Teste | Status |
|----|--------|---------|-------|--------|
| 3b.3.1 | Criar repo GitHub | 0.5 dia | — | ⏳ |
| 3b.3.2 | Terraform: RDS PostgreSQL | 2 dias | terraform plan | ⏳ |
| 3b.3.3 | Migrar Flyway scripts | 1 dia | Verify | ⏳ |
| 3b.3.4 | Outputs exportáveis | 0.5 dia | terraform output | ⏳ |
| 3b.3.5 | GitHub Actions CD | 1 dia | Test | ⏳ |
| 3b.3.6 | Secrets management | 1 dia | Verify | ⏳ |
| 3b.3.7 | Branch protection | 0.5 dia | Config | ⏳ |
| 3b.3.8 | README + RFC PostgreSQL | 1 dia | Review | ⏳ |

### Repositório 4: Aplicação Principal (fiap-tech-challenge-app)

| ID | Tarefa | Duração | Teste | Status |
|----|--------|---------|-------|--------|
| 3b.4.1 | Criar repo GitHub | 0.5 dia | — | ⏳ |
| 3b.4.2 | Migrar código | 1 dia | mvn compile | ⏳ |
| 3b.4.3 | Atualizar pom.xml | 0.5 dia | Build test | ⏳ |
| 3b.4.4 | Docker-compose local | 0.5 dia | docker-compose up | ⏳ |
| 3b.4.5 | Registrar outputs | 0.5 dia | Verify | ⏳ |
| 3b.4.6 | GitHub Actions build/test | 2 dias | Test run | ⏳ |
| 3b.4.7 | GitHub Actions deploy K8s | 1 dia | Dry-run | ⏳ |
| 3b.4.8 | Secrets (DB, JWT, API_GW) | 1 dia | Verify | ⏳ |
| 3b.4.9 | Branch protection | 0.5 dia | Config | ⏳ |
| 3b.4.10 | README | 1 dia | Review | ⏳ |

### Cross-Repo Tasks

| ID | Tarefa | Duração | Status |
|----|--------|---------|--------|
| 3b.X.1 | Setup GitHub org + user soat-architecture | 0.5 dia | ⏳ |
| 3b.X.2 | Configurar GitHub Teams | 1 dia | ⏳ |
| 3b.X.3 | GitHub Secrets template | 1 dia | ⏳ |
| 3b.X.4 | Atualizar documentação repos | 0.5 dia | ⏳ |

---

## 🟡 FASE 3c: Lambda Serverless + API Gateway (Semanas 6-8)

### Subfase 3c.1: Lambda Desenvolvimento

| ID | Tarefa | Duração | Teste | Status |
|----|--------|---------|-------|--------|
| 3c.1.1 | Design API Gateway | 1 dia | Postman | ⏳ |
| 3c.1.2 | Lambda handler CPF→JWT | 2 dias | Unit test | ⏳ |
| 3c.1.3 | CPF validation logic | 1 dia | Unit test | ⏳ |
| 3c.1.4 | JWT generation RS256 | 1 dia | Verify signature | ⏳ |
| 3c.1.5 | Error handling | 0.5 dia | Test edge cases | ⏳ |
| 3c.1.6 | Structured logging JSON | 1 dia | Verify CloudWatch | ⏳ |

### Subfase 3c.2: Integração com App Spring Boot

| ID | Tarefa | Duração | Teste | Status |
|----|--------|---------|-------|--------|
| 3c.2.1 | LoginController.login() | 1 dia | Unit test | ⏳ |
| 3c.2.2 | Lambda invocation wrapper | 1 dia | Integration test | ⏳ |
| 3c.2.3 | SecurityConfig update | 1 dia | Security test | ⏳ |
| 3c.2.4 | Postman collection | 0.5 dia | Manual test | ⏳ |
| 3c.2.5 | Integration test | 1 dia | Pass | ⏳ |
| 3c.2.6 | Documentar novo fluxo | 1 dia | Review | ⏳ |

### Subfase 3c.3: API Gateway AWS

| ID | Tarefa | Duração | Teste | Status |
|----|--------|---------|-------|--------|
| 3c.3.1 | Console AWS API Gateway | 1 dia | Manual | ⏳ |
| 3c.3.2 | Lambda integration | 1 dia | Test via console | ⏳ |
| 3c.3.3 | CORS headers | 0.5 dia | Postman | ⏳ |
| 3c.3.4 | Throttling/quotas | 1 dia | Load test | ⏳ |
| 3c.3.5 | CloudWatch monitoring | 0.5 dia | Check dashboard | ⏳ |
| 3c.3.6 | Documentar endpoint | 0.5 dia | Review | ⏳ |

---

## 🟢 FASE 3d: Observabilidade (Semanas 9-10)

### Subfase 3d.1: Setup Plataforma

| ID | Tarefa | Duração | Opção | Status |
|----|--------|---------|-------|--------|
| 3d.1.1 | Datadog vs New Relic | 1 dia | ADR-008 | ⏳ |
| 3d.1.2 | Trial account | 0.5 dia | — | ⏳ |
| 3d.1.3 | Agent Spring Boot | 1 dia | Build & test | ⏳ |
| 3d.1.4 | K8s DaemonSet | 1 dia | kubectl get pods | ⏳ |
| 3d.1.5 | Lambda CloudWatch | 1 dia | Verify logs | ⏳ |
| 3d.1.6 | Structured logging | 1.5 dias | grep logs | ⏳ |

### Subfase 3d.2: Métricas & Dashboards

| ID | Métrica | KPI Alvo | Duração | Status |
|----|---------|----------|---------|--------|
| 3d.2.1 | API latency (P95) | < 200ms | 1 dia | ⏳ |
| 3d.2.2 | CPU/Memory utilization | < 80% avg | 0.5 dia | ⏳ |
| 3d.2.3 | DB connection pool | < 20 active | 0.5 dia | ⏳ |
| 3d.2.4 | Ordem de Serviço volume | Daily trend | 1 dia | ⏳ |
| 3d.2.5 | JWT generation rate | Baseline | 0.5 dia | ⏳ |
| 3d.2.6 | Error rates | < 1% | 1 dia | ⏳ |
| 3d.2.7 | Business metrics | Custom | 1 dia | ⏳ |
| 3d.2.8 | Executive dashboard | 1-page | 1 dia | ⏳ |

### Subfase 3d.3: Alertas & SLOs

| ID | Alerta | Threshold | Action | Status |
|----|--------|-----------|--------|--------|
| 3d.3.1 | SLOs | Availability 99.5%, Latency P99 < 300ms | Doc | ⏳ |
| 3d.3.2 | High latency | P95 > 300ms | Slack alert | ⏳ |
| 3d.3.3 | High error rate | 5xx > 2% | PagerDuty | ⏳ |
| 3d.3.4 | Pod evictions | CPU/Memory pressure | Scale warning | ⏳ |
| 3d.3.5 | DB connection failure | Can't connect | Critical | ⏳ |
| 3d.3.6 | Lambda cold start | Duration > 2s | Info log | ⏳ |
| 3d.3.7 | Runbooks | Each alert → wiki | Documentation | ⏳ |

---

## 🟣 FASE 3e: Documentação + Vídeo (Semanas 11-12)

### Subfase 3e.1: Documentação Arquitetural

| ID | Documento | Arquivo | Duração | Status |
|----|-----------|---------|---------|--------|
| 3e.1.1 | Diagrama ER expandido | [DiagramaER.md] | 1 dia | ⏳ |
| 3e.1.2 | Arquitetura 4 repos | [Arquitetura4Repos.md] | 1 dia | ⏳ |
| 3e.1.3 | Sequência autenticação | [SequenciaAuth.md] | 0.5 dia | ⏳ |
| 3e.1.4 | Sequência abertura OS | [SequenciaAbertura.md] | 0.5 dia | ⏳ |
| 3e.1.5 | Diagrama deployment | [DeploymentDiagram.md] | 1 dia | ⏳ |
| 3e.1.6 | ADRs finalizadas | [ADRs.md] | 0.5 dia | ⏳ |
| 3e.1.7 | RFCs finalizadas | [RFCs.md] | 0.5 dia | ⏳ |
| 3e.1.8 | Decision matrix | [DecisionMatrix.md] | 0.5 dia | ⏳ |
| 3e.1.9 | Índice docs | [docs/indice.md] | 0.5 dia | ⏳ |

### Subfase 3e.2: README & Guias Operacionais

| ID | Guia | Arquivo | Duração | Status |
|----|------|---------|---------|--------|
| 3e.2.1 | README principal | [README.md] | 1 dia | ⏳ |
| 3e.2.2 | Deploy Fase 3 | [docs/operacional/deploy-fase3.md] | 1 dia | ⏳ |
| 3e.2.3 | Troubleshooting | [docs/operacional/troubleshooting.md] | 1 dia | ⏳ |
| 3e.2.4 | Observabilidade | [docs/operacional/datadog-setup.md] | 1 dia | ⏳ |
| 3e.2.5 | Postman collection | Final update | 0.5 dia | ⏳ |

### Subfase 3e.3: Vídeo de Demonstração (15 min max)

| # | Segmento | Duração | Conteúdo |
|---|----------|---------|----------|
| 1 | Intro | 1 min | Visão geral do desafio |
| 2 | Arquitetura | 3 min | 4 repos, Lambda, monitoramento (screenshare) |
| 3 | Demo Auth | 2 min | Postman POST /authenticate (Lambda real) |
| 4 | Demo OS | 3 min | Abrir OS, mudar status, email |
| 5 | Demo Monitoramento | 4 min | Datadog/New Relic dashboards, alertas |
| 6 | Conclusão | 2 min | Repositórios, documentação, links |

**Tarefas Vídeo**:
| ID | Tarefa | Duração | Status |
|----|--------|---------|--------|
| 3e.3.1 | Preparar ambiente demo | 1 dia | ⏳ |
| 3e.3.2 | Roteiro vídeo | 0.5 dia | ⏳ |
| 3e.3.3 | Gravação + edição | 3 dias | ⏳ |
| 3e.3.4 | Upload YouTube | 0.5 dia | ⏳ |
| 3e.3.5 | QA vídeo | 1 dia | ⏳ |

### Subfase 3e.4: Entrega Final

| ID | Tarefa | Documento | Duração | Status |
|----|--------|-----------|---------|--------|
| 3e.4.1 | Documento PDF | [docs/Fase03/DocumentoFinal.md] | 1 dia | ⏳ |
| 3e.4.2 | Checklist entrega | [docs/Fase03/ChecklistEntrega.md] | 0.5 dia | ⏳ |
| 3e.4.3 | Confirmar soat-architecture | Admin x4 repos | 0.5 dia | ⏳ |
| 3e.4.4 | Validação final | Públicos + acessíveis | 0.5 dia | ⏳ |

---

## 📊 Mapa de Dependências (Critical Path)

```
┌─────────────────────────────────────────────────────────┐
│                   FASE 3a (Semanas 1-2)                 │
│           Planejamento & Análise & Diagramas             │
│              (Bloqueador de tudo!)                       │
└────────────────────────┬────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        ↓                ↓                ↓
    ┌────────┐       ┌────────┐      ┌────────┐
    │FASE 3b │       │FASE 3b │      │FASE 3b │
    │Lambda  │       │K8s     │      │DB      │  (Paralelo - Semanas 3-5)
    └────────┘       └────────┘      └────────┘
        │                ↑                │
        └────────────────┼────────────────┘
                         ↓
                   ┌─────────────┐
                   │  FASE 3b    │
                   │   App       │  (Depende de outputs)
                   └─────────────┘
                         ↓
                   ┌─────────────┐
                   │  FASE 3c    │
                   │   Lambda    │  (Semanas 6-8)
                   │   + API GW  │
                   └─────────────┘
                         ↓
                   ┌─────────────┐
                   │  FASE 3d    │
                   │ Observab.   │  (Semanas 9-10)
                   └─────────────┘
                         ↓
                   ┌─────────────┐
                   │  FASE 3e    │
                   │  Doc + Video│  (Semanas 11-12)
                   └─────────────┘
```

---

## ⚠️ Riscos & Mitigações

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|---------|-----------|
| Lambda cold start > 2s | Alto | Médio | Provisioned concurrency + warmup |
| Separação repos quebra build | Médio | Alto | CI/CD integrado, outputs documentados |
| Datadog over-budget | Médio | Médio | Trial 30 dias, depois New Relic |
| ADRs/RFCs incompletos | Baixo | Médio | 3 reviewers, checklist |
| Vídeo ruim (áudio) | Baixo | Médio | 3 takes antes de publicar |
| DB restoration timeout | Baixo | Alto | Backup automated, testado monthly |

---

## 🎯 Decisão: Datadog vs New Relic?

### Recomendação: **Começar com New Relic (Free Tier)**

| Critério | New Relic | Datadog |
|----------|-----------|---------|
| Custo inicial | 🟢 Grátis | 🟡 ~$31/dia |
| Free tier | 🟢 Unlimited 1 ano | 🟡 14 dias |
| Métricas K8s | 🟢 Excelente | 🟢 Excelente |
| APM | 🟢 Nativo | 🟢 Nativo |
| Alertas | 🟢 Sim | 🟢 Sim |
| Curva aprendizado | 🟡 Média | 🟡 Média |

**Plano**: Usar New Relic na Fase 3. Se orçamento disponível depois, migrar para Datadog.

---

## 📚 Documentação Fase 3

```
docs/Fase03/
├── ADRs.md                          ← 8 decisões arquiteturais
├── RFCs.md                          ← 5 decisões técnicas
├── DiagramaER.md                    ← Mermaid ER + SQL
├── DiagramasSequencia.md            ← 3+ sequências
├── Arquitetura4Repos.md             ← Overview 4 repos
├── DeploymentDiagram.md             ← K8s + RDS + Lambda + API GW
├── EstruturaN4Repos.md              ← Layout repos
├── PlanoMigracao.md                 ← Estratégia migração
├── LambdaArchitecture.md            ← Lambda serverless details
├── Observabilidade.md               ← New Relic/Datadog setup
├── SLOs.md                          ← Definições SLO
├── DecisionMatrix.md                ← Trade-offs analisados
├── PlanoFase3Completo.md            ← Este arquivo
├── DocumentoFinal.md                ← Consolidação final
└── ChecklistEntrega.md              ← 100+ itens verificação
```

---

## ✅ Próximos Passos Imediatos

1. **Hoje**: Revisar este plano, confirmar timeline
2. **Amanhã**: Iniciar Fase 3a (ADRs/RFCs)
3. **Fim de semana**: Diagramas ER e Sequência
4. **Próxima semana**: Iniciar Fase 3b (Repos)

---

## 📞 Suporte & Escalações

- **Arquiteto**: Decisões de design, ADRs, RFCs
- **DevOps**: Terraform, CI/CD, Kubernetes
- **Backend Lead**: Lambda, integração app
- **Tech Lead**: Comunicação cross-repo, timelines

---

**Status Geral**: 🔵 Planejamento 100% | Implementação 0%

Próximo milestone: Fim de Fase 3a (14 dias)


