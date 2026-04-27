# Architecture Decision Records (ADRs) - Fase 3

**Status**: Em Desenvolvimento  
**Versão**: 1.0  
**Última Atualização**: 2026-04-27

---

## ADR-001: Separação em 4 Repositórios Independentes

**Status**: ✅ Aprovado  
**Data de Decisão**: 2026-04-27  

### Contexto
A aplicação Fase 2 estava monolítica em um único repositório. Para atingir escala corporativa com múltiplas equipes e ciclos de release independentes, é necessário separar responsabilidades.

### Decisão
Migrar para **4 repositórios independentes**:
1. `fiap-tech-challenge-lambda-auth` - Autenticação serverless
2. `fiap-tech-challenge-k8s-terraform` - Infraestrutura Kubernetes
3. `fiap-tech-challenge-db-terraform` - Infraestrutura banco de dados
4. `fiap-tech-challenge-app` - Aplicação principal Spring Boot

### Justificativa
✅ **Deploy independente**: Lambda pode ser atualizada sem redeploy da app  
✅ **Escalabilidade em equipes**: DevOps, Backend, SRE com autonomia  
✅ **Versionamento semântico por componente**: Rastreabilidade clara  
✅ **CI/CD paralelo**: Reduz tempo total de build/test/deploy  
✅ **Governança diferenciada**: Cada repo tem branch protection, reviewers e SLAs apropriados  

### Trade-offs
❌ Mais complexo na primeira instalação  
❌ Sincronização de outputs entre repos (K8s→App, DB→App)  
❌ Orquestração de releases (cuidado com versões incompatíveis)  

### Mitigação
- Outputs de Terraform exportados como GitHub environment variables
- Versionamento explícito de imagens Docker (nunca usar `latest`)
- Release checklist documentado em cada repo
- Documentação de dependências inter-repos

---

## ADR-002: Autenticação via Lambda Serverless

**Status**: ✅ Aprovado  
**Data de Decisão**: 2026-04-27  

### Contexto
Novo requisito: validar CPF via função serverless antes de gerar JWT, removendo essa responsabilidade da aplicação principal.

### Decisão
Implementar autenticação com:
- **API Gateway** (AWS API Gateway) para roteamento
- **Lambda** para validação de CPF e geração de JWT
- **Spring Boot** consome JWT (não gera mais)

### Arquitetura
```
User → POST /authenticate {cpf}
  ↓
API Gateway
  ↓
Lambda:
  1. Validar formato CPF
  2. Query: SELECT * FROM clientes WHERE cpf = ?
  3. Gerar JWT RS256 (1h expiry)
  4. return {token, expiry}
  ↓
Spring Boot:
  1. Armazena JWT em SecurityContext
  2. Usa para requisições subsequentes
```

### Justificativa
✅ **Escalabilidade**: Serverless = auto-scaling transparente  
✅ **Isolamento**: Autenticação desacoplada da aplicação  
✅ **Reutilizabilidade**: Múltiplas apps podem consumir o mesmo endpoint  
✅ **Custo reduzido**: Pagamento apenas por execução (estamosm em ~1-2 chamadas por minuto)  

### Trade-offs
❌ Latência adicional: ~200-300ms por autenticação  
❌ Cold start da Lambda: ~3s na primeira chamada  
❌ Gerenciamento adicional de secrets (JWT signing key em AWS Secrets Manager)  

### Mitigação
- Provisioned concurrency (manter 1-2 instâncias warm)
- Caching de tokens no lado cliente (storage local)
- Load test obrigatório (target P95 < 500ms)
- Fallback: opção de manutenção de JWT em memoria no app

---

## ADR-003: Infraestrutura AWS + Terraform

**Status**: ✅ Aprovado  
**Data de Decisão**: 2026-04-27  

### Contexto
Infraestrutura em nuvem deve ser reproducível, versionada e gerenciável por código (IaC).

### Decisão
- **Provedor**: AWS
- **Compute**: EKS (Kubernetes gerenciado)
- **Banco**: RDS PostgreSQL 15+
- **Serverless**: Lambda + API Gateway
- **IaC**: Terraform

### Stack Arquitetura
```
AWS Account
├── VPC (privada)
│   ├── EKS Cluster
│   │   └── Nodes (t3.medium, min=2, max=10)
│   └── RDS PostgreSQL (db.t4g.micro, 20GB SSD)
├── Lambda (em region pública)
├── API Gateway
└── CloudWatch + New Relic integration
```

### Justificativa
✅ **AWS**: Vasta experiência do mercado, documentação excelente  
✅ **EKS**: Kubernetes gerenciado reduz overhead operacional  
✅ **RDS**: Backups automáticos, failover, patches gerenciados  
✅ **Terraform**: IaC versionável, modular, reutilizável  

### Trade-offs
❌ **Vendor lock-in**: AWS específico (difícil migrar depois)  
❌ **Custo**: Estimado ~$200-300/mês em Fase 3  
❌ **Curva aprendizado**: Terraform + AWS CLI + EKS concepts  

### Mitigação
- Usar template Terraform reutilizável (facilita migração futura)
- Documentação clara de cada módulo (VPC, EKS, RDS)
- Estimativa de custos mensal revisada
- Considerar alternativa: Azure + Kubernetes gerenciado se orçamento tight

---

## ADR-004: PostgreSQL como Banco Relacional

**Status**: ✅ Aprovado  
**Data de Decisão**: 2026-04-27  

### Contexto
Banco de dados para domínio de negócio (oficina mecânica) que requer integridade referencial forte, queries complexas e relatórios analíticos.

### Decisão
Manter **PostgreSQL 15+** em AWS RDS.

### Modelo Relacional (Resumido)
```
clientes (id, cpf, nome, email)
├── veiculos (id, cliente_id, placa, modelo)
│   └── ordens_servico (id, veiculo_id, status, data_abertura)
│       └── itens_os (id, ordem_id, peca_id, servico_id, quantidade, preco_unitario_snapshot)
├── pecas (id, nome, valor)
└── servicos (id, nome, valor)
```

### Justificativa
✅ **ACID compliance**: Crítico para ordem de serviço + pagamentos  
✅ **JSON support**: Flexibilidade (ex: dados adicionais sem schema change)  
✅ **RDS managed**: Reduz ops overhead (backups, patches, replicação)  
✅ **Performance**: Índices B-tree excelentes, queries complexas otimizadas  
✅ **Suporte Flyway**: Migrations versionadas  

### Trade-offs
❌ Não é NoSQL (se precisar scale infinita + schema flexibility)  
❌ Scaling horizontal limitado (read replicas complexas, write sempre master)  

### Mitigação
- Índices bem planejados (EXPLAIN ANALYZE)
- Particionamento de tabelas grandes (ex: ordre_servico por data)
- Read replicas para relatórios pesados
- Query profiling e tuning contínuo

---

## ADR-005: Kubernetes com HPA (CPU + Memory)

**Status**: ✅ Aprovado  
**Data de Decisão**: 2026-04-27  

### Contexto
Escalabilidade automática necessária para lidar com picos de carga (dia útil vs. noite, horário de pico).

### Decisão
Usar **Kubernetes HPA** (Horizontal Pod Autoscaler) com múltiplas métricas:
- **CPU**: target 70% utilization
- **Memória**: target 80% utilization
- **Min replicas**: 2 (alta disponibilidade)
- **Max replicas**: 10 (safety limit para custo)

### Configuração HPA
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: oficina-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: oficina-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Justificativa
✅ **Resiliência**: 2 replicas minimum (10+ seconds RTO)  
✅ **Escalabilidade automática**: Sem intervenção manual  
✅ **Eficiência de custos**: Down-scale automaticamente quando carga baixa  
✅ **Multiple metrics**: CPU + Memory (não só CPU)  

### Trade-offs
❌ Mais complexo que deploy estático  
❌ Precisa de tuning fino (CPU 70%, Memory 80% são targets, não exactos)  
❌ Load test obrigatório para validar comportamento  

### Mitigação
- Load testing durante Fase 3d
- Monitoramento contínuo de HPA events
- Alertas para eviction de pods
- Documentação de troubleshooting

---

## ADR-006: CI/CD com GitHub Actions

**Status**: ✅ Aprovado  
**Data de Decisão**: 2026-04-27  

### Contexto
Cada repositório necessita automação completa sem terceiros (Jenkins, GitLab CI, etc).

### Decisão
Usar **GitHub Actions** para todos os 4 repositórios:

**Lambda Repo**:
```
Push → Build → Unit tests → Docker build → ECR push → Manual Approval → Deploy Lambda
```

**K8s Terraform Repo**:
```
Push → terraform validate → terraform plan → Manual Approval → terraform apply
```

**DB Terraform Repo**:
```
Push → terraform validate → terraform plan → Manual Approval → terraform apply
```

**App Repo**:
```
Push → Maven test → SonarQube scan → Docker build → ECR push → Manual Approval → Deploy K8s
```

### Justificativa
✅ **Nativo GitHub**: Sem dependências externas  
✅ **Secrets management**: GitHub Secrets integrado  
✅ **Manual approvals**: Prod deploy requer autorização (branch protection)  
✅ **Logs arquivados**: Histórico de deploys  

### Trade-offs
❌ Vendor lock-in GitHub  
❌ Se sair de GitHub, migração complexa  
❌ Features limitadas vs. Datadog/Jenkins  

### Mitigação
- Documentar workflows em YAML versionado
- Usar ações reutilizáveis (DRY principle)
- Considerar migração futura para GitLab se necessário

---

## ADR-007: Observabilidade com New Relic

**Status**: ✅ Aprovado  
**Data de Decisão**: 2026-04-27  

### Contexto
Visibilidade operacional é crítica: métricas, logs, traces, alerts e dashboards para SLOs corporativos.

### Decisão
**New Relic** como plataforma de observabilidade:
- APM (Java + Lambda)
- Infrastructure monitoring (K8s DaemonSet)
- Logs estruturados (JSON)
- Custom dashboards
- Alert policies

### Stack Observabilidade
```
App Spring Boot → New Relic APM agent
App Spring Boot → Micrometer → New Relic Exporter
Lambda → CloudWatch → New Relic Log Ingest
K8s Nodes → New Relic Infrastructure agent (DaemonSet)
│
└→ New Relic Dashboard: 
   ├── Volume de OS por dia
   ├── Tempo médio por status
   ├── Latência API P95
   ├── CPU/Memory K8s
   └── Error rate & Apdex score
```

### Justificativa
✅ **Free tier**: 100GB/mês por 1 ano (suficiente para MVP)  
✅ **APM nativo**: Java e Lambda com zero-config  
✅ **K8s integration**: Observabilidade automática de pods/nodes  
✅ **Construtor de dashboards**: Sem código YAML complexo  

### Trade-offs
❌ Custo após free tier: ~$31/dia ($900/mês)  
❌ Alternativa Datadog pode ser melhor em alguns casos  
❌ Vendor lock-in observabilidade  

### Mitigação
- Reavaliar após 3 meses de uso
- Documentar como usar New Relic (runbooks)
- Considerar downgrade para observabilidade open-source (Prometheus + Grafana) se orçamento tight

---

## ADR-008: Segregação de Conhecimento via README Completo

**Status**: ✅ Aprovado  
**Data de Decisão**: 2026-04-27  

### Contexto
4 repositórios separados aumentam risco de silos de conhecimento e onboarding lento.

### Decisão
Cada repositório terá **README.md completo com**:
- Descrição do propósito
- Stack técnico (linguagens, frameworks)
- Diagrama arquitetura específica
- Setup local (com Docker Compose)
- Deploy (manual + CI/CD steps)
- Troubleshooting
- Links para outros repos
- Links para documentação externa

### Template README (Mínimo)
```markdown
# fiap-tech-challenge-{component}

## 🎯 Propósito
[Descrição clara]

## 🛠️ Tech Stack
- Language: ...
- Framework: ...
- Runtime: ...

## 📊 Arquitetura
[Mermaid diagram]

## 🚀 Quick Start
```bash
# Local setup
docker-compose up
```
## 📋 Deploy
[CI/CD steps]

## 🔗 Links Relacionados
- [App Principal](...)
- [Documentação Geral](...)
```

### Justificativa
✅ **Onboarding rápido**: Novo dev entende repo em 15 min  
✅ **Reduz silos**: Conhecimento documentado > verbal  
✅ **Facilita manutenção**: Suporte operacional mais rápido  

### Trade-offs
❌ Duplicação potencial de conteúdo  

### Mitigação
- Template único de README (source of truth)
- Cross-referencing entre repos
- Documentação central em Fase3/docs/

---

## Decisões Futuras (A Documentar em Fase 3a)

- **ADR-009**: Estratégia de versionamento (semântico vs outro)
- **ADR-010**: Secrets management (GitHub Secrets vs AWS Secrets Manager vs HashiCorp Vault)
- **ADR-011**: Backup & Disaster Recovery (RDS snapshots policy)
- **ADR-012**: Disaster recovery RPO/RTO targets
- **ADR-013**: Disaster recovery strategy (multi-region vs local only)

---

## Status de Aprovação

| ADR | Arquiteto | Tech Lead | Backend Lead | DevOps | Status Final |
|-----|-----------|-----------|--------------|--------|-------------|
| 001 | ✅ | ✅ | ✅ | ✅ | **APROVADO** |
| 002 | ✅ | ✅ | ✅ | ✅ | **APROVADO** |
| 003 | ✅ | ✅ | ✅ | ✅ | **APROVADO** |
| 004 | ✅ | ✅ | ✅ | ✅ | **APROVADO** |
| 005 | ✅ | ✅ | ✅ | ✅ | **APROVADO** |
| 006 | ✅ | ✅ | ✅ | ✅ | **APROVADO** |
| 007 | ✅ | ✅ | ✅ | ✅ | **APROVADO** |
| 008 | ✅ | ✅ | ✅ | ✅ | **APROVADO** |
