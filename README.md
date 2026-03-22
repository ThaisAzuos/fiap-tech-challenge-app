# 🛠️ Oficina Mecânica API

API REST para gestão de oficina mecânica (Cadastro, Estoque, O.S., Autenticação JWT).

## 🚀 Quick Start (Docker)

Suba a aplicação completa (API + Banco + MailHog) com um comando:

```bash
# Limpar e iniciar
docker-compose down --remove-orphans
docker-compose up --build -d
```

**Acessos:**
- **API:** `http://localhost:8080`
- **Swagger:** `http://localhost:8080/swagger-ui/index.html`
- **MailHog (Emails):** `http://localhost:8025`

**Parar:**
```bash
docker-compose down
```

---

## ☸️ Kubernetes (K8s)

**Deploy:**
```bash
# Aplica todos os manifestos (App, Banco, MailHog, HPA, Secrets)
kubectl apply -f .
```

**Validar:**
```bash
kubectl get pods,svc,hpa
```

**Parar/Limpar:**
```bash
kubectl delete -f .
```

---

## 🧪 Testes e Qualidade

**Unitários/Integração:**
```bash
mvn clean test
```

**Automatizado (sobe MailHog + roda testes):**
```bash
./scripts/test-local.sh
```

**Compose dedicado para testes (somente MailHog):**
```bash
docker compose -f docker-compose.test.yml up -d mailhog
./mvnw clean test
docker compose -f docker-compose.test.yml down
```

**Exemplos úteis do script:**
```bash
# Roda somente um teste especifico
./scripts/test-local.sh -- -Dtest=OpenApiDocumentationTest

# Mantem cache Maven (sem clean) e para MailHog no final
./scripts/test-local.sh --no-clean --down
```

**SonarQube (Local):**
```bash
# Subir Sonar
docker-compose -f docker-compose.sonar.yml up -d

# Executar análise
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<SEU_TOKEN>
```

---

## 📨 Teste de Fluxo (Postman)

Importe `Oficina_Mecanica.postman_collection.json` e siga a ordem:

1. **Autenticação:** `POST /login` (Gera token JWT automático para as próximas chamadas).
2. **Cadastro:** Clientes, Veículos, Peças.
3. **Atendimento:** Abrir O.S., Adicionar Peças, Mudar Status.
4. **Verificação:** Checar e-mails no MailHog (`http://localhost:8025`).

---

## 📂 Documentação e Estrutura

- **`src/`**: Código fonte Java 21 + Spring Boot 3.
- **`docs/`**: Documentação detalhada ([Índice Completo](docs/indice.md)).
  - **ADRs**: Decisões arquiteturais.
  - **Operacional**: Guias do MailHog e SonarQube.
- **`terraform/`**: Infraestrutura as Code (AWS).
- **`k8s-*.yaml`**: Manifestos Kubernetes.

---

## 🛠️ Tecnologias Principais
Java 21, Spring Boot 3, PostgreSQL, Docker/Compose, Kubernetes, Terraform, MailHog, SonarQube.
