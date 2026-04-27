# Fase 3b - Plano de Ação (Executável)

**Data Início**: 2026-04-27  
**Duração**: 3 semanas  
**Status**: 🟢 INICIADA

---

## 📊 Tarefas Por Dia

### DIA 1 (2026-04-27): Setup Inicial

#### Manhã: Criar Repositórios GitHub

**T1.1: Criar 4 repos vazios no GitHub**

```bash
# Navegue até https://github.com/organizations/grupo37/repositories

Criar:
1. fiap-tech-challenge-lambda-auth
2. fiap-tech-challenge-k8s-terraform
3. fiap-tech-challenge-db-terraform
4. fiap-tech-challenge-app  (migração)

Settings (cada repo):
- Visibility: Public
- Initialize: NO (começar vazio)
- Default branch: main
```

**T1.2: Add `soat-architecture` como Admin**

```bash
# Para cada repositório:
Settings → Collaborators → Add people
- Username: soat-architecture
- Role: Admin
```

**T1.3: Enable Branch Protection**

```bash
# Settings → Branches → Add rule
Branch name pattern: main

☑ Require a pull request before merging
  ☑ Require 2 approvals
  ☑ Dismiss stale PR approvals
☑ Require status checks (deixa vazio por enquanto)
☑ Require branches up to date
☑ Include administrators
```

**T1.4: Create GitHub Secrets** (cada repo)

Repo: lambda-auth
```
Settings → Secrets and variables → Actions → New secret

AWS_ACCESS_KEY_ID: [do seu AWS]
AWS_SECRET_ACCESS_KEY: [do seu AWS]
ECR_REGISTRY: [seu 123456.dkr.ecr.us-east-1.amazonaws.com]
```

Repo: k8s-terraform
```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_ROLE_TO_ASSUME: arn:aws:iam::123456:role/terraform-role
TF_STATE_BUCKET: seu-bucket-s3-terraform
```

Repo: db-terraform
```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_ROLE_TO_ASSUME
TF_STATE_BUCKET
```

Repo: app
```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
ECR_REGISTRY
SONARQUBE_HOST
SONARQUBE_TOKEN
```

#### Tarde: Preparar Estrutura Local

**T1.5: Clone & Prepare com Arquivos**

```bash
# No seu PC, crie diretórios:
mkdir -p ~/projects/fiap-phase3-repos
cd ~/projects/fiap-phase3-repos

# Clone vazio ou prepare templates (você já tem em docs/)
cp docs/Fase03/_TEMPLATE-Lambda-Auth-Completo.md .
cp docs/Fase03/_TEMPLATE-K8s-Terraform.md .

# Estruturar:
mkdir lambda-auth k8s-terraform db-terraform app
```

---

### DIA 2 (2026-04-28): Repo 1 - Lambda Auth

**T2.1: Criar estrutura Lambda**

```bash
cd lambda-auth

# Crie arquivos baseado em _TEMPLATE-Lambda-Auth-Completo.md
# Estrutura:
touch README.md
touch .gitignore
touch requirements.txt
touch Dockerfile

mkdir -p src tests tests terraform scripts
mkdir -p .github/workflows

# Crie todos os Python files (ver template)
touch src/__init__.py
touch src/handler.py
touch src/auth_service.py
touch src/cpf_validator.py
touch src/jwt_generator.py
touch src/db_client.py
touch src/logger.py

# Crie testes
touch tests/__init__.py
touch tests/conftest.py
touch tests/test_handler.py
touch tests/test_cpf_validator.py
touch tests/test_jwt_generator.py

# Crie Terraform
touch terraform/main.tf
touch terraform/variables.tf
touch terraform/lambda_role.tf

# Crie workflow
touch .github/workflows/build.yml
touch .github/workflows/deploy.yml

# Scripts
touch scripts/build.sh scripts/test-local.sh scripts/deploy.sh
```

**T2.2: Copiar conteúdo dos templates**

Ver _TEMPLATE-Lambda-Auth-Completo.md e copiar cada seção para o arquivo correspondente.

**T2.3: Git init & push**

```bash
cd lambda-auth

git init
git add .
git commit -m "initial: lambda auth structure"
git branch -M main
git remote add origin https://github.com/grupo37/fiap-tech-challenge-lambda-auth.git
git push -u origin main

# Verify no GitHub
```

**T2.4: Test local**

```bash
python -m venv venv
source venv/bin/activate  # ou venv\Scripts\activate no Windows
pip install -r requirements.txt
pytest tests/ -v --cov=src
```

---

### DIA 3 (2026-04-29): Repo 2 - K8s Terraform

**T3.1: Criar estrutura Terraform**

```bash
cd k8s-terraform

# Estrutura (minimal)
touch README.md
touch .gitignore
touch main.tf
touch variables.tf
touch outputs.tf
touch terraform.tfvars

mkdir -p modules/{vpc,eks,security}
mkdir -p envs
mkdir -p .github/workflows
mkdir -p scripts

# Modules
touch modules/vpc/{main.tf,variables.tf,outputs.tf}
touch modules/eks/{main.tf,node_group.tf,variables.tf,outputs.tf}
touch modules/security/main.tf

# Environments
touch envs/{dev,staging,prod}.tfvars

# Workflows
touch .github/workflows/{validate,plan,apply}.yml

# Scripts
touch scripts/{init,plan,apply,destroy}.sh
```

**T3.2: Copiar conteúdo**

Ver _TEMPLATE-K8s-Terraform.md e popule os arquivos.

**T3.3: Terraform validation local**

```bash
cd k8s-terraform
terraform init  # (use -backend-config=skip-backend-validation se não tiver S3)
terraform fmt
terraform validate
```

**T3.4: Push**

```bash
git init
git add .
git commit -m "initial: k8s terraform structure"
git branch -M main
git remote add origin https://github.com/grupo37/fiap-tech-challenge-k8s-terraform.git
git push -u origin main
```

---

### DIA 4 (2026-04-30): Repo 3 - DB Terraform

**T4.1: Criar estrutura RDS**

```bash
cd db-terraform

# Arquivos
touch README.md
touch .gitignore
touch main.tf
touch variables.tf
touch outputs.tf

mkdir -p modules/rds
mkdir -p migrations
mkdir -p envs
mkdir -p .github/workflows

# RDS Terraform
touch modules/rds/{main.tf,backup.tf,security_group.tf,variables.tf,outputs.tf}

# Migrations
touch migrations/V1__initial_schema.sql
touch migrations/V2__add_notifications.sql

# Environments
touch envs/{dev,staging,prod}.tfvars

# Workflow
touch .github/workflows/apply.yml
```

**T4.2: Copiar templates**

Baseado no EstruturaN4Repos.md e padrões já definidos.

**T4.3: Terraform validation**

```bash
terraform validate
```

**T4.4: Push**

```bash
git init
git add .
git commit -m "initial: db terraform structure"
git branch -M main
git remote add origin https://github.com/grupo37/fiap-tech-challenge-db-terraform.git
git push -u origin main
```

---

### DIA 5 (2026-05-01): Repo 4 - App Migração

**T5.1: Preparar App para novo repo**

```bash
# De dentro do repo atual
cp -r src pom.xml Dockerfile docker-compose.yml ../app-temp/

cd ../app-temp

# Remove Terraform section do pom.xml
# (edit manualmente: remover <terraform> dependencies)

# Remove Fase03 docs
rm -rf docs/Fase03

# Atualizar pom.xml para remover Terraform
```

**T5.2: Estrutura App**

```bash
# No novo diretório app/

mkdir -p .github/workflows
mkdir -p k8s
mkdir -p src/main/{java,resources}
mkdir -p src/test/java

cp Dockerfile docker-compose.yml README.md .
touch .gitignore
```

**T5.3: GitHub Workflows para App**

```bash
touch .github/workflows/{build-test,docker-build,deploy}.yml

# Ver templates em EstruturaN4Repos.md
```

**T5.4: K8s Manifests**

```bash
touch k8s/{deployment,service,hpa,configmap,secret}.yaml
```

**T5.5: Git init & push**

```bash
cd app

git init
git add .
git commit -m "initial: migrate spring boot app from monolith"
git branch -M main
git remote add origin https://github.com/grupo37/fiap-tech-challenge-app.git
git push -u origin main
```

---

## 📋 Checklist Rápido Dia 1-5

- [ ] Dia 1: 4 repos criados no GitHub
- [ ] Dia 1: soat-architecture adicionado como admin x4
- [ ] Dia 1: Branch protection configurada x4
- [ ] Dia 1: GitHub Secrets criados x4
- [ ] Dia 2: Lambda repo com código Python funcional
- [ ] Dia 2: Lambda testes passando (`pytest`)
- [ ] Dia 3: K8s Terraform repo com módulos
- [ ] Dia 3: Terraform validate passando
- [ ] Dia 4: DB Terraform repo completo
- [ ] Dia 4: Terraform validate passando
- [ ] Dia 5: App repo migrado (sem Terraform)
- [ ] Dia 5: K8s manifests preparados

---

## 🚀 Semana 2: Primeiros Deployments

**Semana 2 (5-9 de maio)**:

1. **Provisionar Infraestrutura**
   - [ ] Terraform apply: K8s cluster
   - [ ] Terraform apply: RDS database
   - [ ] Validar kubeconfig

2. **Deploy Lambda Auth**
   - [ ] Build Docker image
   - [ ] Push para ECR
   - [ ] Deploy via Terraform (manual)

3. **Deploy App**
   - [ ] Configurar ECR repository
   - [ ] Build Docker image da app
   - [ ] Deploy manifest K8s (manual)

4. **Integração**
   - [ ] Lambda retorna JWT válido
   - [ ] App usa JWT de Lambda
   - [ ] HPA responde a carga

---

## 🎯 Próximas Fases

Após Fase 3b (5 dias + 2 semanas refined):

- **Fase 3c**: Lambda + API Gateway detalhado (Semana 6-8)
- **Fase 3d**: Observabilidade (Semana 9-10)
- **Fase 3e**: Documentação + Vídeo (Semana 11-12)

---

## 🔗 Links Úteis

- [QuickStart-Fase3b.md](./QuickStart-Fase3b.md)
- [_TEMPLATE-Lambda-Auth-Completo.md](./_TEMPLATE-Lambda-Auth-Completo.md)
- [_TEMPLATE-K8s-Terraform.md](./_TEMPLATE-K8s-Terraform.md)
- [EstruturaN4Repos.md](./EstruturaN4Repos.md)
- [PlanoFase3Completo.md](./PlanoFase3Completo.md)

---

**Você está pronto para começar DIA 1?** 🚀


