# Quick Start - Fase 3b

**Status**: 🟢 INICIADA em 2026-04-27  
**Duração Estimada**: 3 semanas (Semana 3-5 do projeto)  
**Objetivo**: Criar 4 repositórios com CI/CD base funcional

---

## 📋 Checklist Rápido (Dia 1-2)

### Pré-requisitos
- ✅ Conta GitHub com permissão de criar repositórios
- ✅ AWS Account com credenciais configuradas
- ✅ Acesso a: AWS ECR, EKS, RDS, Secrets Manager
- ✅ Docker instalado localmente
- ✅ Terraform 1.5+ instalado

### Tarefas de Setup (Paralelas)

| Tarefa | Responsável | Duração | Status |
|--------|-------------|---------|--------|
| **T1: Criar Repo Lambda Auth** | DevOps | 2h | ⏳ |
| **T2: Criar Repo K8s Terraform** | DevOps | 2h | ⏳ |
| **T3: Criar Repo DB Terraform** | DBA+DevOps | 2h | ⏳ |
| **T4: Criar Repo App** | Backend | 1h | ⏳ |
| **T5: Setup GitHub Secrets (x4)** | DevOps | 1h | ⏳ |
| **T6: Enable Branch Protection (x4)** | Tech Lead | 0.5h | ⏳ |

---

## 🏗️ Estrutura Por Repositório

### Repo 1: fiap-tech-challenge-lambda-auth

**URL a criar**: `https://github.com/grupo37/fiap-tech-challenge-lambda-auth`

Arquivos para copiar:
- ✅ Todos em `_templates/lambda-auth/`

**Passos**:
1. Criar repositório vazio no GitHub
2. Descompactar/copiar arquivos do template
3. Comitar: `git add . && git commit -m "initial: lambda auth structure"`
4. Push:  `git push origin main`
5. Configurar branch protection

**Duração**: ~20 min

---

### Repo 2: fiap-tech-challenge-k8s-terraform

**URL a criar**: `https://github.com/grupo37/fiap-tech-challenge-k8s-terraform`

Arquivos para copiar:
- ✅ Todos em `_templates/k8s-terraform/`

**Passos**:
1. Criar repositório vazio
2. Descompactar/copiar arquivos
3. Comitar estrutura
4. Push
5. Configurar branch protection

**Duração**: ~20 min

---

### Repo 3: fiap-tech-challenge-db-terraform

**URL a criar**: `https://github.com/grupo37/fiap-tech-challenge-db-terraform`

Arquivos para copiar:
- ✅ Todos em `_templates/db-terraform/`

**Duração**: ~20 min

---

### Repo 4: fiap-tech-challenge-app

**URL a criar**: `https://github.com/grupo37/fiap-tech-challenge-app`

**Este é migração da atual!**

Passos:
1. Clone do repositório atual
2. Remove terraform/ section do pom.xml
3. Remove docs/Fase03 (fica só no master)
4. Limpa .git history (opcional)
5. Push como novo repo

**Duração**: ~30 min

---

## 🔧 Setup GitHub Secrets

Cada repositório precisa de GitHub Secrets:

### lambda-auth repo
```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
ECR_REGISTRY  # ex: 123456789.dkr.ecr.us-east-1.amazonaws.com
```

### k8s-terraform repo
```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_ROLE_TO_ASSUME  # ex: arn:aws:iam::123456789:role/terraform-role
```

### db-terraform repo
```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_ROLE_TO_ASSUME
```

### app repo
```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
ECR_REGISTRY
SONARQUBE_HOST
SONARQUBE_TOKEN
```

---

## 🔐 Branch Protection (Todos os 4 Repos)

Configurar em: **Settings → Branches → Add rule for 'main'**

```
☑️ Require a pull request before merging
   ☑️ Require approvals: 2
   ☑️ Dismiss stale pull request approvals

☑️ Require status checks to pass before merging
   - build (ou maven-build)
   - test (ou maven-test)  
   - terraform-validate (se Terraform)

☑️ Require branches to be up to date before merging

☑️ Include administrators in above restrictions
```

---

## 📂 Próximos Passos

### Semana 1 de 3b (Dias 1-5)
- [ ] Criar 4 repositórios
- [ ] Setup CI/CD workflows
- [ ] Configure GitHub Secrets & branch protection
- [ ] Teste local de cada componente

### Semana 2 de 3b (Dias 6-10)
- [ ] Implementar Lambda auth function
- [ ] Provisionar Terraform K8s
- [ ] Provisionar Terraform DB
- [ ] Primeiro deploy teste

### Semana 3 de 3b (Dias 11-15)
- [ ] Integração inter-repos
- [ ] Teste de deploys automáticos
- [ ] Documentação final Fase 3b

---

## 🎯 Você Quer Que Eu...?

**Opção A**: Criar todos os templates em arquivos separados (você copia depois)  
**Opção B**: Criar os repos diretamente via GitHub CLI (se tiver credenciais)  
**Opção C**: Criar um script de setup automático (bash/Python)  

Qual você prefere? 🤔


