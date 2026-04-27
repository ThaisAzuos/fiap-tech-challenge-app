# Fase 3 - Índice e Mapa de Navegação

**Data**: 2026-04-27  
**Status**: 🟢 Fase 3a 100% | 🟡 Fase 3b 90% Pronta

---

## 📚 Documentação por Fase

### 🟢 FASE 3a: Planificação & Análise (COMPLETA ✅)

| Arquivo | Foco | Ler Se... |
|---------|------|----------|
| **PlanoFase3Completo.md** | Plano 12 semanas com 5 subfases | Precisa de visão geral |
| **ADRs.md** | 8 decisões arquiteturais aprovadas | Quer entender por quê das decisões |
| **RFCs.md** | 8 decisões técnicas (branch, deploy, secrets) | Quer implementação técnica |
| **DiagramasSequencia.md** | 4 diagramas mermaid + componentes | Entender fluxos visuais |
| **EstruturaN4Repos.md** | Template dos 4 repositórios | Visão geral de cada repo |

### 🟡 FASE 3b: Setup 4 Repositórios (PRONTA PARA EXECUÇÃO 🚀)

| Arquivo | Conteúdo | Use Para... |
|---------|----------|-----------|
| **QuickStart-Fase3b.md** | Checklist rápido + pré-requisitos | Começar agora mesmo |
| **Fase3b-PlanoAcao-Diario.md** | Plano dia-a-dia com tarefas | Entender cronograma |
| **Fase3b-Copy-Paste-Files.md** | Todos os arquivos prontos | Copiar e colar código |
| **_TEMPLATE-Lambda-Auth-Completo.md** | 15+ arquivos Lambda (Python, tests, Terraform) | Setup Lambda repo |
| **_TEMPLATE-K8s-Terraform.md** | Terraform modules (VPC, EKS) | Setup K8s repo |

### ⏳ FASE 3c: Lambda + API Gateway (PRÓXIMA)

*(A implementar em Semana 6-8)*

### ⏳ FASE 3d: Observabilidade (PRÓXIMA)

*(A implementar em Semana 9-10)*

### ⏳ FASE 3e: Documentação + Vídeo (PRÓXIMA)

*(A implementar em Semana 11-12)*

---

## 🗂️ Os 4 Repositórios

### 1️⃣ fiap-tech-challenge-lambda-auth

```
🟢 Status: Template Completo + Copy-Paste Pronto

Ficheiros:
- README.md
- .gitignore, requirements.txt, Dockerfile
- .github/workflows/ (build, deploy)
- src/ (7 Python files: handler, auth_service, cpf_validator, jwt_generator, db_client, logger, __init__)
- tests/ (conftest, test_handler, test_cpf_validator, test_jwt_generator)
- terraform/ (main, lambda_role, api_gateway, variables, outputs)
- scripts/ (build, test-local, deploy)

Referência: _TEMPLATE-Lambda-Auth-Completo.md
```

### 2️⃣ fiap-tech-challenge-k8s-terraform

```
🟢 Status: Template + Main Files Pronto

Ficheiros:
- README.md
- main.tf, variables.tf, outputs.tf
- modules/ (vpc, eks, security)
- .github/workflows/ (validate, plan, apply)
- envs/ (dev, staging, prod).tfvars
- scripts/ (init, plan, apply, destroy)
- .gitignore

Referência: _TEMPLATE-K8s-Terraform.md
```

### 3️⃣ fiap-tech-challenge-db-terraform

```
🟢 Status: Template Pronto

Ficheiros:
- README.md
- main.tf (RDS), variables.tf, outputs.tf
- modules/rds/ (main, backup, security_group)
- migrations/ (Flyway V1, V2)
- .github/workflows/apply.yml
- envs/ (dev, staging, prod).tfvars
- .gitignore

Referência: EstruturaN4Repos.md + Fase3b-Copy-Paste-Files.md
```

### 4️⃣ fiap-tech-challenge-app

```
🟢 Status: Migração do Atual

Origem: Projeto atual E:\FIAP - Software Architecture\fiap-tech-challenge-oficina

Passos:
1. Copy: src/, pom.xml, Dockerfile, docker-compose.yml
2. Remove: terraform/, docs/Fase03/
3. Add: k8s/ manifests, .github/workflows/, docker-compose.yml
4. Editar: pom.xml (remover Terraform dependencies)

Referência: Fase3b-Copy-Paste-Files.md
```

---

## ⚡ Como Começar (Agora!)

### Opção A: Começar imediatamente
1. Leia: **QuickStart-Fase3b.md** (10 min)
2. Siga: **Fase3b-PlanoAcao-Diario.md** (5 dias, 8h/dia)
3. Copy-paste: **Fase3b-Copy-Paste-Files.md** (templates prontos)

### Opção B: Entender primeiro
1. Revise: **PlanoFase3Completo.md** (overview)
2. Estude: **ADRs.md + RFCs.md** (decisões técnicas)
3. Vistoria: **DiagramasSequencia.md** (fluxos)
4. Então execute Opção A

### Opção C: Deep dive técnico
1. **EstruturaN4Repos.md** (arquitectura por repo)
2. **_TEMPLATE-Lambda-Auth-Completo.md** (detalhes lambda)
3. **_TEMPLATE-K8s-Terraform.md** (Terraform)
4. Execute templates

---

## 📊 Cronograma Executável

```
Semana 1 de 3b (Dias 1-5)
├── Dia 1: Setup GitHub (repos, secrets, branch protection)
├── Dia 2: Lambda repo + Python code + tests
├── Dia 3: K8s Terraform repo
├── Dia 4: DB Terraform repo
└── Dia 5: App migração

Semana 2 de 3b (Dias 6-10)
├── Dia 6: Terraform init + plan (localmente)
├── Dia 7: Deploy K8s cluster (terraform apply)
├── Dia 8: Deploy RDS database (terraform apply)
├── Dia 9: Build + push Docker images
└── Dia 10: Deploy app em K8s (manual)

Semana 3 de 3b (Dias 11-15)
├── Dia 11: Teste Lambda auth endpoint
├── Dia 12: Teste integração app + Lambda
├── Dia 13: Teste HPA + scaling
├── Dia 14: Validação CI/CD workflows
└── Dia 15: Documentação + cleanup
```

---

## ✅ Checklist Fase 3b Completo

**DIA 1 (GitHub Setup)**
- [ ] 4 repos criados (lambda-auth, k8s-terraform, db-terraform, app)
- [ ] soat-architecture admin em todos
- [ ] Branch protection (main branch conforme ADR)
- [ ] GitHub Secrets x4 populados

**DIA 2-5 (Código + Deploy)**
- [ ] Lambda repo: código Python + tests + Terraform
- [ ] K8s repo: Terraform modules + workflows
- [ ] DB repo: RDS config + migrations
- [ ] App repo: migração + k8s manifests

**SEMANA 2 (Infraestrutura)**
- [ ] terraform init local (validado)
- [ ] terraform apply K8s (cluster vivo)
- [ ] terraform apply DB (RDS rodando)
- [ ] Docker images em ECR
- [ ] App runando em K8s

**VALIDAÇÕES**
- [ ] Lambda retorna JWT válido
- [ ] App recebe JWT de Lambda
- [ ] K8s HPA respondendo a carga
- [ ] GitHub Actions workflows executando
- [ ] Logs estruturados em CloudWatch

---

## 🎯 Próximos Milestones

| Milestone | Quando | Foco |
|-----------|--------|------|
| **Repos + CI/CD** | Semana 5 | 4 repositórios online com workflows |
| **Infraestrutura Pronta** | Semana 6 | EKS + RDS + Lambda live |
| **Autenticação Funcional** | Semana 6-7 | Lambda auth + JWT + API Gateway |
| **App em K8s** | Semana 7 | Aplicação escalando automaticamente |
| **Observabilidade** | Semana 9-10 | New Relic dashboards + alertas |
| **Documentação Final** | Semana 11-12 | PDF + Vídeo demo (15 min) |
| **Entrega Final** | Semana 12 | Portal do Aluno + links |

---

## 📌 Quick Links

**Dentro deste Repositório (docs/Fase03/)**:
```
├── PlanoFase3Completo.md           🟢 12 semanas
├── ADRs.md                         🟢 8 decisões arquiteturais
├── RFCs.md                         🟢 8 decisões técnicas
├── DiagramasSequencia.md           🟢 4 diagramas
├── EstruturaN4Repos.md            🟢 Overview 4 repos
├── QuickStart-Fase3b.md           🟡 Comece daqui!
├── Fase3b-PlanoAcao-Diario.md     🟡 Plano 5 dias
├── Fase3b-Copy-Paste-Files.md     🟡 Código copy-paste
├── _TEMPLATE-Lambda-Auth.md       🟡 Lambda completo
└── _TEMPLATE-K8s-Terraform.md     🟡 K8s essencial
```

**Arquivos Externos**:
- GitHub: https://github.com/grupo37 (criar repos)
- AWS Console: RDS, EKS, Lambda, ECR, Secrets Manager
- Documentation: docs/Fase03/* + READMEs em cada repo

---

## 🚀 Você Quer?

### A) Iniciar **HOJE** (siga isto):
1. Abra: `QuickStart-Fase3b.md`
2. Siga: `Fase3b-PlanoAcao-Diario.md`
3. Use: `Fase3b-Copy-Paste-Files.md`
4. Resultado: 4 repos vivos em 5 dias ✅

### B) Entender tudo (siga isto):
1. Leia: `PlanoFase3Completo.md`
2. Estude: `ADRs.md` + `RFCs.md`
3. Vise: `DiagramasSequencia.md`
4. Depois execute A)

### C) Debugar/Troubleshoot:
- Erro build Lambda? → `_TEMPLATE-Lambda-Auth-Completo.md`
- Erro Terraform? → `_TEMPLATE-K8s-Terraform.md`
- Qual arquivo criar? → `Fase3b-Copy-Paste-Files.md`
- Qual é o plano? → `Fase3b-PlanoAcao-Diario.md`

---

## 💡 Dicas Finais

1. **Copy-paste é seu amigo**: Use `Fase3b-Copy-Paste-Files.md`
2. **Valide tudo local**: `terraform validate`, `pytest`
3. **Commit pequenininho**: "initial: repo setup"
4. **Branch protection first**: Configure logo no Dia 1
5. **Secrets environment**: Nunca commite credenciais
6. **Terraform state seguro**: Use S3 backing
7. **Tests rodar sempre**: GitHub Actions deve passar antes de merge

---

**Total de Documentação**: 10+ files  
**Total de Código Pronto**: ~2.500 linhas copy-paste  
**Tempo Estimado**: 3 semanas (5 dias setup + 2 semanas refine)  

🎯 **Status**: Pronto para Decolar! 🚀


