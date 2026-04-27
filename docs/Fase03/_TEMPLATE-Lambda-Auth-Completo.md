# Lambda Auth - Template Completo

Este documento contém todos os arquivos para criar o `fiap-tech-challenge-lambda-auth` repository.

## 📋 Arquivos a Criar

```
fiap-tech-challenge-lambda-auth/
├── README.md                          ← [ver abaixo]
├── .gitignore                         ← [ver abaixo]
├── requirements.txt
├── Dockerfile
├── .github/workflows/
│   ├── build.yml
│   ├── validate.yml
│   └── deploy.yml
├── src/
│   ├── __init__.py
│   ├── handler.py
│   ├── auth_service.py
│   ├── cpf_validator.py
│   ├── jwt_generator.py
│   ├── db_client.py
│   └── logger.py
├── tests/
│   ├── __init__.py
│   ├── conftest.py
│   ├── test_handler.py
│   ├── test_cpf_validator.py
│   └── test_jwt_generator.py
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   ├── lambda_role.tf
│   ├── api_gateway.tf
│   └── outputs.tf
└── scripts/
    ├── build.sh
    ├── test-local.sh
    └── deploy.sh
```

---

## 📄 README.md

```markdown
# Lambda Authentication Function (fiap-tech-challenge-lambda-auth)

Função serverless AWS Lambda para autenticação via CPF de clientes e geração de JWT.

## 🎯 Propósito

- ✅ Validar CPF do cliente
- ✅ Consultar cliente no banco (PostgreSQL RDS)
- ✅ Gerar JWT RS256 (validade 1h)
- ✅ Retornar token para acesso às APIs

## 🛠️ Stack Técnico

- **Runtime**: Python 3.11
- **AWS**: Lambda, API Gateway, Secrets Manager, CloudWatch
- **Database**: PostgreSQL 15 (RDS)
- **Authentication**: JWT RS256
- **Testing**: pytest, moto (AWS mocking)
- **CI/CD**: GitHub Actions

## 🚀 Quick Start Local

### Pré-requisitos
```bash
python 3.11+
pip
aws-cli
aws-sam-cli (para teste local)
```

### Setup
```bash
# Clone o repositório
git clone https://github.com/grupo37/fiap-tech-challenge-lambda-auth.git
cd fiap-tech-challenge-lambda-auth

# Crie um virtual environment
python -m venv venv
source venv/bin/activate  # Linux/Mac
# ou
venv\Scripts\activate     # Windows

# Instale dependências
pip install -r requirements.txt
```

###Teste Local
```bash
# Execute testes
pytest tests/ -v --cov=src

# Invoque Lambda localmente com SAM
sam local start-api

# Em outro terminal, faça uma request
curl -X POST http://localhost:3000/authenticate \
  -H "Content-Type: application/json" \
  -d '{"cpf": "12345678901"}'
```

## 🔑 Secrets Necessários (AWS Secrets Manager)

```
/oficina/prod/jwt-signing-key  (private RSA key)
/oficina/prod/db-password      (RDS password)
```

## 📊 API Endpoint

### POST /authenticate

**Request**:
```json
{
  "cpf": "12345678901"
}
```

**Response (200)**:
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiry": 3600,
  "user_id": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (401)**:
```json
{
  "error": "CPF not found or inactive",
  "error_code": "AUTH_INVALID_CPF"
}
```

## 📧 Structured Logging

Todos os logs em JSON para integração New Relic:

```json
{
  "timestamp": "2026-04-27T14:30:00Z",
  "level": "INFO",
  "logger": "auth_service",
  "message": "Cliente autenticado",
  "correlationId": "req-abc123",
  "cpf": "12345678901",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "duration_ms": 245,
  "status": "SUCCESS"
}
```

## 🚢 Deploy para AWS

### Via GitHub Actions (automático)
```bash
git push origin main  # Trigger CI/CD
# Build → Test → Docker push ECR → Deploy Lambda
```

### Manual (desenvolvimento)
```bash
cd terraform/
terraform init
terraform plan
terraform apply  # Requer MFA
```

## 📈 Monitoramento (CloudWatch + New Relic)

Links:
- **CloudWatch Logs**: https://console.aws.amazon.com/cloudwatch/
- **New Relic APM**: https://one.newrelic.com/

Métricas rastreadas:
- Latência por CPF
- Taxa de erro (CPF inválido, DB error)
- Throughput (requisições/min)
- Cold start duration

## 🔗 Links Relacionados

- [Adicionar Links do README dos outros repos aqui]
- [Plano Fase 3](../docs/Fase03/PlanoFase3Completo.md)
- [ADRs](../docs/Fase03/ADRs.md)
- [Estrutura 4 Repos](../docs/Fase03/EstruturaN4Repos.md)

## 👥 Suporte

- **Issues**: GitHub Issues
- **Docs**: Ver wiki/

## 📄 Licença

FIAP Tech Challenge 2026
```

---

## 📄 .gitignore

```
# Python
__pycache__/
*.py[cod]
*$py.class
*.so
.Python
env/
venv/
*.egg-info/
dist/
build/

# AWS
.aws/
*.aws.credentials

# IDE
.vscode/
.idea/
*.swp
*.swo

# Terraform
*.tfstate
*.tfstate.*
terraform.tfvars
.terraform/

# OS
.DS_Store
Thumbs.db

# Logs
*.log
```

---

## 📄 requirements.txt

```
# Core
aws-lambda-powertools==2.23.0  # AWS Lambda logging, tracing, utilities
boto3==1.26.137                 # AWS SDK
psycopg2-binary==2.9.6         # PostgreSQL adapter

# JWT
PyJWT==2.8.0
cryptography==40.0.2

# Validation
email-validator==2.0.0
pydantic==2.0.0

# Testing
pytest==7.4.0
pytest-cov==4.1.0
pytest-asyncio==0.21.0
moto==4.1.10  # AWS mocking

# Linting
black==23.7.0
flake8==6.0.0
pylint==2.17.4
```

---

## 📄 Dockerfile

```dockerfile
# Multi-stage build for Lambda
FROM public.ecr.aws/lambda/python:3.11 as builder

# Install dependencies
COPY requirements.txt .
RUN pip install -r requirements.txt -t "${LAMBDA_TASK_ROOT}"

# Final stage
FROM public.ecr.aws/lambda/python:3.11

# Copy dependencies from builder
COPY --from=builder ${LAMBDA_TASK_ROOT} ${LAMBDA_TASK_ROOT}

# Copy lambda function
COPY src/ ${LAMBDA_TASK_ROOT}/

# Set handler
CMD ["handler.lambda_handler"]
```

---

## 📄 .github/workflows/build.yml

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
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.11'
          cache: 'pip'
      
      - name: Install dependencies
        run: |
          python -m pip install --upgrade pip
          pip install -r requirements.txt
      
      - name: Lint with flake8
        run: flake8 src --count --max-line-length=120 --statistics
      
      - name: Format check with black
        run: black --check src
      
      - name: Run tests
        run: pytest tests/ -v --cov=src --cov-report=xml
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage.xml
          flags: unittests
          name: codecov-umbrella
```

---

## 📄 .github/workflows/deploy.yml

```yaml
name: Deploy Lambda

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production  # Requer aprovação manual
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.11'
      
      - name: Build Docker image
        run: |
          docker build -t oficina-lambda-auth:${{ github.sha }} .
          aws ecr get-login-password | docker login --username AWS --password-stdin ${{ secrets.ECR_REGISTRY }}
          docker tag oficina-lambda-auth:${{ github.sha }} ${{ secrets.ECR_REGISTRY }}/oficina-lambda-auth:latest
          docker tag oficina-lambda-auth:${{ github.sha }} ${{ secrets.ECR_REGISTRY }}/oficina-lambda-auth:${{ github.sha }}
          docker push ${{ secrets.ECR_REGISTRY }}/oficina-lambda-auth:latest
          docker push ${{ secrets.ECR_REGISTRY }}/oficina-lambda-auth:${{ github.sha }}
      
      - name: Deploy Lambda
        run: |
          cd terraform/
          terraform init -backend-config="bucket=${{ secrets.TERRAFORM_STATE_BUCKET }}" || true
          terraform plan -var="image_uri=${{ secrets.ECR_REGISTRY }}/oficina-lambda-auth:${{ github.sha }}" -out=tfplan
          terraform apply tfplan
```

---

## 📄 src/handler.py

```python
"""
Lambda handler entry point for authentication
"""
import json
import logging
from typing import Any, Dict

from aws_lambda_powertools import Logger, Tracer
from pydantic import ValidationError

from auth_service import AuthService
from logger import setup_logger

logger = setup_logger(__name__)
tracer = Tracer()

auth_service = AuthService()


@tracer.capture_lambda_handler
def lambda_handler(event: Dict[str, Any], context: Any) -> Dict[str, Any]:
    """
    Main Lambda handler for authentication.
    
    Expected event:
    {
        "body": "{\"cpf\": \"12345678901\"}"
    }
    """
    correlation_id = event.get("requestContext", {}).get("requestId", "unknown")
    
    try:
        # Parse request
        body = json.loads(event.get("body", "{}"))
        cpf = body.get("cpf")
        
        if not cpf:
            return error_response(400, "CPF is required", correlation_id)
        
        # Authenticate
        result = auth_service.authenticate(cpf, correlation_id)
        
        return {
            "statusCode": 200,
            "body": json.dumps({
                "token": result["token"],
                "expiry": result["expiry"],
                "user_id": result["user_id"],
                "correlation_id": correlation_id
            }),
            "headers": {
                "Content-Type": "application/json",
                "Access-Control-Allow-Origin": "*"
            }
        }
    
    except ValueError as e:
        logger.exception("Authentication failed", extra={"error": str(e), "correlation_id": correlation_id})
        return error_response(401, str(e), correlation_id)
    
    except Exception as e:
        logger.exception("Unexpected error", extra={"error": str(e), "correlation_id": correlation_id})
        return error_response(500, "Internal server error", correlation_id)


def error_response(status: int, message: str, correlation_id: str) -> Dict[str, Any]:
    """Generate error response"""
    return {
        "statusCode": status,
        "body": json.dumps({
            "error": message,
            "correlation_id": correlation_id
        }),
        "headers": {
            "Content-Type": "application/json"
        }
    }
```

---

## 📄 src/cpf_validator.py

```python
"""
CPF validation logic
"""
import re


def validate_cpf_format(cpf: str) -> bool:
    """
    Validate CPF format (11 digits, with or without punctuation)
    
    Args:
        cpf: CPF string
    
    Returns:
        bool: True if valid format
    
    Raises:
        ValueError: If invalid format
    """
    # Remove punctuation
    cpf_clean = re.sub(r'[^\d]', '', cpf)
    
    if len(cpf_clean) != 11:
        raise ValueError("CPF must have exactly 11 digits")
    
    if not cpf_clean.isdigit():
        raise ValueError("CPF must contain only digits")
    
    # Check for all same digits (invalid CPF)
    if cpf_clean == cpf_clean[0] * 11:
        raise ValueError("Invalid CPF: all digits are the same")
    
    return True


def calculate_cpf_check_digit(cpf_base: str) -> str:
    """
    Calculate CPF check digit (optional validation).
    """
    # This is optional - business might not want full CPF validation
    # Just format validation might be enough
    pass
```

---

## 📄 src/jwt_generator.py

```python
"""
JWT token generation
"""
import json
import datetime
from typing import Dict, Any

import jwt

from logger import setup_logger

logger = setup_logger(__name__)


class JWTGenerator:
    """Generate and validate JWT tokens"""
    
    def __init__(self, private_key: str, algorithm: str = "RS256"):
        """
        Initialize JWT generator.
        
        Args:
            private_key: RSA private key (PEM format)
            algorithm: JWT algorithm (default: RS256)
        """
        self.private_key = private_key
        self.algorithm = algorithm
    
    def generate(self, user_id: str, cpf: str, expiry_hours: int = 1) -> Dict[str, Any]:
        """
        Generate JWT token.
        
        Args:
            user_id: UUID of authenticated user
            cpf: CPF of user
            expiry_hours: Token expiry in hours
        
        Returns:
            dict: {"token": str, "expiry": int}
        """
        now = datetime.datetime.utcnow()
        expiry = now + datetime.timedelta(hours=expiry_hours)
        
        payload = {
            "user_id": user_id,
            "cpf": cpf,
            "iat": now,
            "exp": expiry,
            "iss": "fiap-oficina-api"
        }
        
        token = jwt.encode(payload, self.private_key, algorithm=self.algorithm)
        
        logger.info("JWT generated", extra={
            "user_id": user_id,
            "expires_in": expiry_hours * 3600
        })
        
        return {
            "token": token,
            "expiry": expiry_hours * 3600
        }
```

---

## 📄 src/auth_service.py

```python
"""
Authentication business logic
"""
from typing import Dict, Any

from cpf_validator import validate_cpf_format
from jwt_generator import JWTGenerator
from db_client import DatabaseClient
from logger import setup_logger

logger = setup_logger(__name__)


class AuthService:
    """Orchestrate authentication flow"""
    
    def __init__(self):
        self.db = DatabaseClient()
        # Load private key from AWS Secrets Manager
        private_key = self.db.get_secret("jwt-signing-key")
        self.jwt_gen = JWTGenerator(private_key)
    
    def authenticate(self, cpf: str, correlation_id: str) -> Dict[str, Any]:
        """
        Authenticate user by CPF.
        
        Args:
            cpf: Customer CPF
            correlation_id: Request correlation ID
        
        Returns:
            dict: {"token": str, "expiry": int, "user_id": str}
        
        Raises:
            ValueError: If CPF invalid or user not found
        """
        # Validate format
        validate_cpf_format(cpf)
        
        # Query database
        user = self.db.get_cliente_by_cpf(cpf)
        
        if not user:
            logger.warning("Cliente not found", extra={
                "cpf": cpf,
                "correlation_id": correlation_id
            })
            raise ValueError("CPF not found or inactive")
        
        if not user.get("ativo"):
            logger.warning("Cliente inactive", extra={
                "user_id": user.get("id"),
                "correlation_id": correlation_id
            })
            raise ValueError("User account is inactive")
        
        # Generate JWT
        token_data = self.jwt_gen.generate(
            user_id=user["id"],
            cpf=cpf,
            expiry_hours=1
        )
        
        logger.info("Autenticacao sucesso", extra={
            "user_id": user["id"],
            "correlation_id": correlation_id
        })
        
        return {
            "token": token_data["token"],
            "expiry": token_data["expiry"],
            "user_id": user["id"]
        }
```

---

## 📄 src/db_client.py

```python
"""
Database client for RDS PostgreSQL
"""
import json
import psycopg2
import boto3

from logger import setup_logger

logger = setup_logger(__name__)


class DatabaseClient:
    """Connect and query PostgreSQL RDS"""
    
    def __init__(self):
        self.secrets_client = boto3.client("secretsmanager")
        self.conn = None
    
    def _get_connection(self):
        """Get or create DB connection"""
        if self.conn is None:
            # Get credentials from Secrets Manager
            secret = self._get_secret("db-connection-string")
            
            self.conn = psycopg2.connect(secret)
        
        return self.conn
    
    def get_secret(self, secret_name: str) -> str:
        """Retrieve secret from AWS Secrets Manager"""
        try:
            response = self.secrets_client.get_secret_value(
                SecretId=f"/oficina/prod/{secret_name}"
            )
            return response.get("SecretString")
        except Exception as e:
            logger.error("Failed to retrieve secret", extra={"secret": secret_name})
            raise
    
    def get_cliente_by_cpf(self, cpf: str) -> dict:
        """Query cliente by CPF"""
        try:
            conn = self._get_connection()
            cursor = conn.cursor()
            
            cursor.execute("""
                SELECT id, cpf, nome, email, ativo
                FROM clientes
                WHERE cpf = %s
            """, (cpf,))
            
            row = cursor.fetchone()
            cursor.close()
            
            if row:
                return {
                    "id": str(row[0]),
                    "cpf": row[1],
                    "nome": row[2],
                    "email": row[3],
                    "ativo": row[4]
                }
            return None
        
        except Exception as e:
            logger.error("DB query failed", extra={"error": str(e)})
            raise

```

---

## 📄 src/logger.py

```python
"""
Structured logging setup
"""
import json
import logging
from typing import Any, Dict

import sys


def setup_logger(name: str) -> logging.Logger:
    """Setup structured JSON logger"""
    logger = logging.getLogger(name)
    
    handler = logging.StreamHandler(sys.stdout)
    
    class JSONFormatter(logging.Formatter):
        def format(self, record):
            log_data = {
                "timestamp": self.formatTime(record, "%Y-%m-%dT%H:%M:%SZ"),
                "level": record.levelname,
                "logger": record.name,
                "message": record.getMessage(),
                "function": record.funcName,
                "line": record.lineno
            }
            
            # Add extra fields
            if hasattr(record, "__dict__"):
                for key, value in record.__dict__.items():
                    if key not in ["name", "msg", "args", "created", "filename", "funcName", "levelname", "levelno", "lineno", "module", "msecs", "message", "module", "pathname", "process", "processName", "relativeCreated", "thread", "threadName"]:
                        log_data[key] = value
            
            return json.dumps(log_data)
    
    handler.setFormatter(JSONFormatter())
    logger.addHandler(handler)
    logger.setLevel(logging.INFO)
    
    return logger
```

---

## 📄 tests/conftest.py

```python
"""
Test fixtures and configuration
"""
import os
import pytest


@pytest.fixture
def aws_credentials():
    """Mocked AWS Credentials for moto"""
    os.environ["AWS_ACCESS_KEY_ID"] = "testing"
    os.environ["AWS_SECRET_ACCESS_KEY"] = "testing"
    os.environ["AWS_SECURITY_TOKEN"] = "testing"
    os.environ["AWS_SESSION_TOKEN"] = "testing"
    os.environ["AWS_DEFAULT_REGION"] = "us-east-1"


@pytest.fixture
def mock_client():
    """Mock cliente data"""
    return {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "cpf": "12345678901",
        "nome": "João Silva",
        "email": "joao@example.com",
        "ativo": True
    }
```

---

## 📄 tests/test_cpf_validator.py

```python
"""
Tests for CPF validation
"""
import pytest

from src.cpf_validator import validate_cpf_format


def test_valid_cpf():
    assert validate_cpf_format("12345678901") is True


def test_invalid_cpf_wrong_length():
    with pytest.raises(ValueError, match="exactly 11 digits"):
        validate_cpf_format("123456789")


def test_invalid_cpf_all_same_digits():
    with pytest.raises(ValueError, match="all digits are the same"):
        validate_cpf_format("11111111111")


def test_cpf_with_punctuation():
    assert validate_cpf_format("123.456.789-01") is True
```

---

## 📄 terraform/main.tf

```hcl
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    # Configure via GitHub Actions or local -backend-config
  }
}

provider "aws" {
  region = var.aws_region
}

# Lambda function
resource "aws_lambda_function" "auth" {
  function_name = "oficina-lambda-auth-${var.environment}"
  role          = aws_iam_role.lambda_role.arn
  
  image_uri = var.image_uri
  package_type  = "Image"
  
  timeout  = 60
  memory_size = 256
  
  environment {
    variables = {
      ENVIRONMENT = var.environment
      DB_HOST     = var.db_host
      REGION      = var.aws_region
    }
  }
  
  vpc_config {
    subnet_ids         = var.private_subnet_ids
    security_group_ids = [aws_security_group.lambda.id]
  }
  
  tags = {
    Environment = var.environment
  }
}

# API Gateway integration
resource "aws_apigatewayv2_integration" "lambda" {
  api_id           = var.api_gateway_id
  integration_type = "AWS_PROXY"
  integration_method = "POST"
  payload_format_version = "2.0"
  target           = aws_lambda_function.auth.arn
}

resource "aws_apigatewayv2_route" "authenticate" {
  api_id    = var.api_gateway_id
  route_key = "POST /authenticate"
  target    = "integrations/${aws_apigatewayv2_integration.lambda.id}"
}

# Lambda permission for API Gateway
resource "aws_lambda_permission" "api_gateway" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.auth.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_gateway_arn}/*"
}
```

---

Todos os arquivos acima podem ser copiados e criados no novo repositório! 

Quer que eu continue com os templates dos outros 3 repositórios?

