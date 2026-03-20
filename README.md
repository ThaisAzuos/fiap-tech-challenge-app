# Oficina Mecanica API - Tech Challenge

API REST para gestao de oficina mecanica, com fluxos de cadastro, estoque, atendimento (ordem de servico) e autenticacao JWT.

Este README foi padronizado para uso operacional no dia a dia: subir ambiente, testar e-mail, validar endpoints e consultar documentacao tecnica.

## Indice

- [Status do projeto](#status-do-projeto)
- [Tecnologias](#tecnologias)
- [Estrutura principal](#estrutura-principal)
- [Como executar localmente com Docker](#como-executar-localmente-com-docker)
- [MailHog (teste de envio de e-mail)](#mailhog-teste-de-envio-de-e-mail)
- [Como executar com Maven (sem Docker da aplicacao)](#como-executar-com-maven-sem-docker-da-aplicacao)
- [Testes](#testes)
- [Kubernetes](#kubernetes)
- [Teste rapido da API (sequencia sugerida)](#teste-rapido-da-api-sequencia-sugerida)
- [Terraform (IaC)](#terraform-iac)
- [SonarQube e qualidade](#sonarqube-e-qualidade)
- [CI/CD](#cicd)
- [Documentacao complementar](#documentacao-complementar)
- [Troubleshooting rapido](#troubleshooting-rapido)

## Status do projeto

- Base da Fase 1 implementada e funcional para os fluxos principais.
- Itens de evolucao (seguranca, cobertura de testes e padronizacao arquitetural completa) estao em andamento.
- Avaliacao detalhada: `AVALIACAO_FASE_1_DETALHADA.md`.

## Tecnologias

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Spring Security + JWT
- Maven
- Docker e Docker Compose
- Kubernetes
- Terraform (IaC)
- Postman e Swagger UI
- SonarQube

## Estrutura principal

- `src/main/java`: codigo principal da aplicacao
- `src/test/java`: testes automatizados
- `k8s-*.yaml`: manifestos Kubernetes
- `modules/oficina-clean-mvp/`: modulo MVP para Fase 2 (Clean Architecture)
- `terraform/`: infraestrutura AWS com Terraform
- `docs/`: toda a documentacao do projeto (ADRs, arquitetura, avaliacao, operacional)
  - `docs/ADRs/`: decisoes arquiteturais formais (ADR-001 a ADR-004)
  - `docs/arquitetura/`: diagramas e comparativos
  - `docs/avaliacao-fase-1/`: analise de conformidade da Fase 1
  - `docs/operacional/`: guias do MailHog, SonarQube e entregaveis
  - `docs/historico/`: registros historicos de implementacao
  - `docs/indice.md`: indice navegavel de todos os documentos
  - `docs/leia-primeiro.md`: ponto de entrada por perfil de leitor

## Como executar localmente com Docker

Suba API, banco e servicos de apoio via Compose:

```bash
docker-compose down --remove-orphans
docker-compose up --build -d
```

Endpoints locais:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- MailHog Web UI: `http://localhost:8025`

Para parar:

```bash
docker-compose down
```

## MailHog (teste de envio de e-mail)

Use o script oficial do projeto para iniciar/parar e inspecionar o MailHog:

```bash
chmod +x mailhog-setup.sh
./mailhog-setup.sh start
./mailhog-setup.sh status
```

### Regra de reuso de container existente

O comando `./mailhog-setup.sh start` aplica reuso automatico para evitar conflito de portas:

1. Reutiliza `mailhog-dev` se ja estiver rodando.
2. Reutiliza outro container da imagem `mailhog/mailhog:latest` se existir.
3. Reutiliza o container que ja estiver expondo a porta `1025`.
4. So cria novo container quando nao houver candidato.

Comandos uteis:

```bash
./mailhog-setup.sh start
./mailhog-setup.sh stop
./mailhog-setup.sh reset
./mailhog-setup.sh logs
./mailhog-setup.sh ui
./mailhog-setup.sh status
```

Guia completo: `docs/operacional/mailhog-setup.md`.

## Como executar com Maven (sem Docker da aplicacao)

Se quiser rodar a API localmente pelo Maven, mantendo dependencias externas em containers:

```bash
./mailhog-setup.sh start
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

## Testes

Executar todos os testes:

```bash
mvn clean test
```

Executar testes de e-mail no perfil `dev`:

```bash
./mailhog-setup.sh start
mvn clean test -Dspring.profiles.active=dev -Dtest=EmailServiceIntegrationTest
```

## Kubernetes

Aplicar os manifestos principais (incluindo MailHog):

```bash
kubectl apply -f k8s-secret.yaml
kubectl apply -f k8s-configmap.yaml
kubectl apply -f k8s-postgres.yaml
kubectl apply -f k8s-mailhog.yaml
kubectl apply -f k8s-deployment.yaml
kubectl apply -f k8s-service.yaml
kubectl apply -f k8s-hpa.yaml
```

Validar recursos:

```bash
kubectl get pods
kubectl get svc
kubectl get hpa
```

Acessar MailHog no cluster:

```bash
kubectl port-forward svc/oficina-mailhog 8025:8025
```

## Teste rapido da API (sequencia sugerida)

1. `POST /login`
2. `POST /api/v1/clientes`
3. `POST /api/v1/veiculos`
4. `POST /api/v1/pecas`
5. `POST /api/v1/atendimento/os`
6. `PATCH /api/v1/atendimento/os/{osId}/status`
7. Verificar e-mail em `http://localhost:8025`

Collection pronta: `Oficina_Mecanica.postman_collection.json`.

## Terraform (IaC)

Comandos basicos:

```bash
cd terraform
terraform init
terraform apply -var="db_password=SUA_SENHA_SEGURA"
```

Para destruir:

```bash
terraform destroy -var="db_password=SUA_SENHA_SEGURA"
```

Detalhes: `terraform/README.md`.

## SonarQube e qualidade

- Compose dedicado do SonarQube: `docker-compose.sonar.yml`
- Guia de ambiente produtivo: `docs/operacional/sonarqube-producao.md`

Analise local rapida (com SonarQube rodando via `docker-compose.sonar.yml`):

```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<SEU_TOKEN>
```

## CI/CD

Workflows ativos em `.github/workflows/`:

| Arquivo | Funcao | Gatilhos |
|---------|--------|----------|
| `sonar.yml` | Build, testes e analise SonarQube | push/PR `main`/`develop`, `workflow_dispatch` |
| `ci-cd.yml` | Build Docker e deploy Kubernetes | push `main` |
| `qodana_code_quality.yml` | Analise estatica Qodana (JetBrains) | push/PR `main` |

Secrets necessarios no repositorio GitHub:

| Secret           | Usado em         | Descricao                                       |
|------------------|------------------|-------------------------------------------------|
| `SONAR_HOST_URL` | `sonar.yml`      | URL do servidor SonarQube                       |
| `SONAR_TOKEN`    | `sonar.yml`      | Token de analise (My Account > Security)        |
| `DOCKER_USERNAME`| `ci-cd.yml`      | Usuario Docker Hub                              |
| `DOCKER_PASSWORD`| `ci-cd.yml`      | Token de acesso Docker Hub                      |
| `KUBE_CONFIG`    | `ci-cd.yml`      | kubeconfig para acesso ao cluster Kubernetes    |

Template de referencia: `.github/workflow-templates/sonar-maven-template.yml`

## Documentacao complementar

- `docs/indice.md` — índice navegável de todos os documentos
- `docs/leia-primeiro.md` — ponto de entrada por perfil (executivo, dev, arquiteto)
- `docs/ADRs/` — decisões arquiteturais formais (ADR-001 a ADR-004)
- `docs/arquitetura/` — diagramas e comparativos de arquitetura
- `docs/avaliacao-fase-1/` — análise de conformidade da Fase 1
- `docs/historico/plano-execucao.md` — plano de execução de 10 dias

## Troubleshooting rapido

Porta `1025` ocupada:

```bash
./mailhog-setup.sh start
./mailhog-setup.sh status
```

Se a porta estiver em uso por processo fora do Docker, libere a porta e execute novamente.

Aplicacao nao sobe no Docker Compose:

```bash
docker-compose logs -f
```

Build Maven com erro de testes:

```bash
mvn -e -DskipTests=false test
```
