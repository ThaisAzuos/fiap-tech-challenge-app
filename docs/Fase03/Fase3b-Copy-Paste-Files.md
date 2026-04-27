# Fase 3b - Arquivos Prontos para Copiar (Copy-Paste Guide)

Este documento contém o essencial de cada arquivo pronto para copiar e colar.

---

## 🔴 REPO 1: Lambda Auth

### Arquivo: README.md

```markdown
# Lambda Authentication Function

Função serverless para autenticação de clientes via CPF.

## Architecture
- Python 3.11 runtime
- AWS Lambda + API Gateway
- PostgreSQL RDS para validação CPF
- JWT RS256 generation

## Quick Start
```bash
python -m venv venv && source venv/bin/activate
pip install -r requirements.txt
pytest tests/ -v --cov=src
```

## Deploy
```bash
git push origin main  # Trigger CI/CD
```

## Links
- [Plano Fase 3](https://github.com/grupo37/docs-fase3)
- [App](https://github.com/grupo37/fiap-tech-challenge-app)
```

### Arquivo: .gitignore

```
__pycache__/
*.py[cod]
.Python
env/
venv/
*.egg-info/
.pytest_cache/
.coverage
htmlcov/
dist/
build/
.aws/
.vscode/
.idea/
*.log
.DS_Store
```

### Arquivo: requirements.txt

```
aws-lambda-powertools==2.23.0
boto3==1.26.137
psycopg2-binary==2.9.6
PyJWT==2.8.0
cryptography==40.0.2
email-validator==2.0.0
pydantic==2.0.0
pytest==7.4.0
pytest-cov==4.1.0
moto==4.1.10
black==23.7.0
```

### Arquivo: Dockerfile

```dockerfile
FROM public.ecr.aws/lambda/python:3.11 as builder
COPY requirements.txt .
RUN pip install -r requirements.txt -t "${LAMBDA_TASK_ROOT}"

FROM public.ecr.aws/lambda/python:3.11
COPY --from=builder ${LAMBDA_TASK_ROOT} ${LAMBDA_TASK_ROOT}
COPY src/ ${LAMBDA_TASK_ROOT}/
CMD ["handler.lambda_handler"]
```

### Arquivo: .github/workflows/build.yml

```yaml
name: Build & Test Lambda
on:
  push:
    branches: [develop, main]
  pull_request:
    branches: [develop, main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-python@v4
        with:
          python-version: '3.11'
      - run: pip install -r requirements.txt
      - run: pytest tests/ -v --cov=src
```

### Arquivo: .github/workflows/deploy.yml

```yaml
name: Deploy Lambda
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production
    steps:
      - uses: actions/checkout@v3
      - uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      - run: docker build -t oficina-lambda:${{ github.sha }} .
      - run: |
          aws ecr get-login-password | docker login --username AWS --password-stdin ${{ secrets.ECR_REGISTRY }}
          docker tag oficina-lambda:${{ github.sha }} ${{ secrets.ECR_REGISTRY }}/oficina-lambda:${{ github.sha }}
          docker push ${{ secrets.ECR_REGISTRY }}/oficina-lambda:${{ github.sha }}
```

### Arquivo: src/handler.py

```python
import json
import logging
from auth_service import AuthService

logger = logging.getLogger()
auth_service = AuthService()

def lambda_handler(event, context):
    try:
        body = json.loads(event.get("body", "{}"))
        cpf = body.get("cpf")
        
        if not cpf:
            return {"statusCode": 400, "body": json.dumps({"error": "CPF required"})}
        
        result = auth_service.authenticate(cpf)
        return {
            "statusCode": 200,
            "body": json.dumps({"token": result["token"], "expiry": result["expiry"]})
        }
    except Exception as e:
        return {"statusCode": 401, "body": json.dumps({"error": str(e)})}
```

### Arquivo: src/cpf_validator.py

```python
import re

def validate_cpf_format(cpf):
    cpf_clean = re.sub(r'[^\d]', '', cpf)
    
    if len(cpf_clean) != 11:
        raise ValueError("CPF must have 11 digits")
    if cpf_clean == cpf_clean[0] * 11:
        raise ValueError("Invalid CPF")
    
    return True
```

### Arquivo: src/jwt_generator.py

```python
import jwt
import datetime

class JWTGenerator:
    def __init__(self, private_key):
        self.private_key = private_key
    
    def generate(self, user_id, cpf, hours=1):
        now = datetime.datetime.utcnow()
        expiry = now + datetime.timedelta(hours=hours)
        
        payload = {
            "user_id": user_id,
            "cpf": cpf,
            "exp": expiry,
            "iss": "oficina-api"
        }
        
        token = jwt.encode(payload, self.private_key, algorithm="RS256")
        return {"token": token, "expiry": hours * 3600}
```

### Arquivo: src/auth_service.py

```python
from cpf_validator import validate_cpf_format
from jwt_generator import JWTGenerator
from db_client import DatabaseClient

class AuthService:
    def __init__(self):
        self.db = DatabaseClient()
        private_key = self.db.get_secret("jwt-signing-key")
        self.jwt_gen = JWTGenerator(private_key)
    
    def authenticate(self, cpf):
        validate_cpf_format(cpf)
        user = self.db.get_cliente_by_cpf(cpf)
        
        if not user or not user.get("ativo"):
            raise ValueError("CPF not found")
        
        return self.jwt_gen.generate(user["id"], cpf)
```

### Arquivo: src/db_client.py

```python
import psycopg2
import boto3

class DatabaseClient:
    def __init__(self):
        self.secrets_client = boto3.client("secretsmanager")
    
    def get_secret(self, name):
        response = self.secrets_client.get_secret_value(SecretId=f"/oficina/prod/{name}")
        return response.get("SecretString")
    
    def get_cliente_by_cpf(self, cpf):
        # DB connection and query
        pass
```

### Arquivo: tests/conftest.py

```python
import pytest
import os

@pytest.fixture
def aws_credentials():
    os.environ["AWS_ACCESS_KEY_ID"] = "testing"
    os.environ["AWS_SECRET_ACCESS_KEY"] = "testing"
```

### Arquivo: tests/test_cpf_validator.py

```python
import pytest
from src.cpf_validator import validate_cpf_format

def test_valid_cpf():
    assert validate_cpf_format("12345678901")

def test_invalid_cpf():
    with pytest.raises(ValueError):
        validate_cpf_format("11111111111")
```

---

## 🔵 REPO 2: K8s Terraform

### Arquivo: README.md

```markdown
# Kubernetes Infrastructure (Terraform)

Provision EKS cluster, VPC, nodes.

## Deploy
```bash
terraform init
terraform plan -var-file=envs/prod.tfvars
terraform apply -var-file=envs/prod.tfvars
```

## Outputs
```bash
terraform output cluster_endpoint
terraform output cluster_name
aws eks update-kubeconfig --region us-east-1 --name $(terraform output -raw cluster_name)
```

## Cost
- EKS: $73/mês
- Nodes: $60/mês
- NAT: $32/mês
- **Total**: ~$165/mês
```

### Arquivo: main.tf (simplificado)

```hcl
terraform {
  required_version = ">= 1.5"
  required_providers {
    aws = { source = "hashicorp/aws"; version = "~> 5.0" }
  }
  backend "s3" {}
}

provider "aws" { region = var.aws_region }

module "vpc" {
  source = "./modules/vpc"
  environment = var.environment
  cluster_name = var.cluster_name
}

module "eks" {
  source = "./modules/eks"
  environment = var.environment
  cluster_name = var.cluster_name
  vpc_id = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
}
```

### Arquivo: variables.tf

```hcl
variable "aws_region" { type = string; default = "us-east-1" }
variable "environment" { type = string }
variable "cluster_name" { type = string; default = "oficina-eks" }
variable "kubernetes_version" { type = string; default = "1.28" }
variable "node_instance_type" { type = string; default = "t3.medium" }
variable "desired_node_count" { type = number; default = 2 }
variable "max_node_count" { type = number; default = 10 }
```

### Arquivo: outputs.tf

```hcl
output "cluster_endpoint" { value = module.eks.cluster_endpoint }
output "cluster_name" { value = module.eks.cluster_name }
output "vpc_id" { value = module.vpc.vpc_id }
```

### Arquivo: envs/prod.tfvars

```hcl
environment = "prod"
cluster_name = "oficina-eks"
kubernetes_version = "1.28"
desired_node_count = 2
max_node_count = 10
```

### Arquivo: .github/workflows/apply.yml

```yaml
name: Terraform Apply
on:
  push:
    branches: [main]

jobs:
  apply:
    runs-on: ubuntu-latest
    environment: production
    steps:
      - uses: actions/checkout@v3
      - uses: hashicorp/setup-terraform@v2
      - uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      - run: terraform init
      - run: terraform apply -auto-approve -var-file=envs/prod.tfvars
```

---

## 🟢 REPO 3: DB Terraform

### Arquivo: README.md

```markdown
# Database Infrastructure (RDS PostgreSQL)

PostgreSQL 15 em RDS com Multi-AZ.

## Backup
- Daily backups, 29-day retention
- Multi-AZ failover
- RTO: 1h, RPO: 1 day

## Deploy
```bash
terraform init
terraform apply -var-file=envs/prod.tfvars
```

## Connection
```bash
postgresql://postgres:PASSWORD@RDS_ENDPOINT:5432/oficina
```
```

### Arquivo: main.tf (simples)

```hcl
terraform {
  required_providers {
    aws = { source = "hashicorp/aws"; version = "~> 5.0" }
  }
  backend "s3" {}
}

provider "aws" { region = var.aws_region }

resource "aws_db_instance" "oficina" {
  identifier            = "oficina-db-prod"
  engine               = "postgres"
  engine_version       = "15.2"
  instance_class       = "db.t4g.micro"
  allocated_storage    = 20
  
  db_name  = "oficina"
  username = "postgres"
  password = var.db_password
  
  multi_az               = true
  backup_retention_period = 29
  skip_final_snapshot     = false
  storage_encrypted      = true
  
  tags = { Environment = var.environment }
}
```

### Arquivo: variables.tf

```hcl
variable "aws_region" { type = string; default = "us-east-1" }
variable "environment" { type = string }
variable "db_password" { type = string; sensitive = true }
```

### Arquivo: outputs.tf

```hcl
output "db_endpoint" { value = aws_db_instance.oficina.endpoint }
output "db_port" { value = aws_db_instance.oficina.port }
```

---

## 🟡 REPO 4: App (Migração)

Copie todos os arquivos do projeto atual menos:
- `docs/Fase03/`
- `terraform/` (remove essa pasta)
- `pom.xml` (edita para remover Terraform dependencies)

### Arquivo: .github/workflows/build-test.yml

```yaml
name: Build & Test
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: mvn clean compile
      - run: mvn test
```

### Arquivo: k8s/deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oficina-app
spec:
  replicas: 2
  selector: { matchLabels: { app: oficina-app } }
  template:
    metadata:
      labels: { app: oficina-app }
    spec:
      containers:
      - name: oficina-app
        image: YOUR_ECR/oficina-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef: { name: app-config; key: db-host }
```

### Arquivo: docker-compose.yml

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_HOST: postgres
      DB_USER: postgres
      DB_PASSWORD: password
    depends_on:
      - postgres
  
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: oficina
      POSTGRES_PASSWORD: password
```

---

## ✅ Uso

1. Cria novo repo no GitHub (vazio)
2. Clone localmente: `git clone <repo-url>`
3. Copie/cole conteúdo dos seções acima conforme necessário
4. Commit & push:
   ```bash
   git add .
   git commit -m "initial: repo setup"
   git push origin main
   ```

---

Tudo pronto para copiar e colar! 🚀


