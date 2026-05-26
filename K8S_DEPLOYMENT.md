# Oficina Mecanica API - Application Repository

Aplicação Spring Boot migrada para rodarem em Kubernetes (EKS).

## 📋 Estrutura

```
.
├── src/                    # Código-fonte Java
├── k8s/                    # Manifestos Kubernetes
├── .github/workflows/      # CI/CD pipelines
├── pom.xml                # Maven configuration
├── Dockerfile             # Container image
└── README.md
```

## 🚀 Quick Start

### Local Development

```bash
# Build
./mvnw clean package

# Run
java -jar target/oficina-*.jar

# Tests
./mvnw test
```

### Kubernetes Deployment

```bash
# Create secrets and ConfigMap first
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml

# Deploy
kubectl apply -f k8s/

# Verify
kubectl get pods -l app=oficina-app
kubectl logs -f deployment/oficina-app
```

## 🔄 CI/CD Pipeline

- **build.yml**: Build e testes em PRs
- **deploy.yml**: Build, push para ECR, e deploy em EKS

## 📦 Kubernetes Manifests

- `deployment.yaml`: Deployment com health checks e resource limits
- `service.yaml`: Service ClusterIP
- `ingress.yaml`: ALB Ingress Controller
- `hpa.yaml`: Horizontal Pod Autoscaler (CPU/Memory)
- `configmap.yaml`: Configurações da aplicação
- `secret.yaml`: Secrets (DB, JWT, etc)
- `serviceaccount.yaml`: Service Account

## 📊 Escalabilidade

- Mínimo: 2 replicas
- Máximo: 10 replicas
- Triggers: CPU 70%, Memory 80%

## 🔐 Segurança

- runAsNonRoot: true
- readOnlyRootFilesystem: true
- Sem privilegios (DROP ALL capabilidades)
- Network policies (recomendado)

