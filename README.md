# fiap-tech-challenge-app

## 🎯 Propósito
Este repositório contém a aplicação principal Spring Boot para o sistema de oficina mecânica. Ela foi refatorada para consumir um serviço de autenticação externo (AWS Lambda) para validação de CPF e geração de JWT, e para rodar em um cluster Kubernetes (EKS) utilizando um banco de dados PostgreSQL gerenciado (RDS).

## 🛠️ Tech Stack
- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.x
- **Banco de Dados**: PostgreSQL (via AWS RDS)
- **ORM**: Spring Data JPA / Hibernate
- **Migrações DB**: Flyway
- **Segurança**: Spring Security (validação JWT RS256)
- **Observabilidade**: New Relic APM
- **Containerização**: Docker
- **Orquestração**: Kubernetes (EKS)

## 📊 Arquitetura
```mermaid
graph TD
    User[Usuário] --> |1. POST /login {cpf}| App[Spring Boot App]
    App --> |2. POST /authenticate {cpf}| LambdaAuth[Lambda Auth Service]
    LambdaAuth --> RDS[RDS PostgreSQL]
    LambdaAuth --> |3. Retorna JWT| App
    App --> |4. Retorna JWT| User
    User --> |5. Requisições com JWT| App
    App -- Valida JWT --> LambdaAuth
    App -- Persistência --> RDS
    App -- Métricas & Logs --> NewRelic[New Relic Platform]
    subgraph Kubernetes Cluster
        App
    end
```

## 🚀 Quick Start (Setup Local)
Para rodar a aplicação localmente, você precisará de:
- Java 21
- Maven
- Docker (opcional, para rodar o PostgreSQL e MailHog localmente)
- Um banco de dados PostgreSQL (pode ser via Docker)
- Uma chave pública RSA (para `API_SECURITY_TOKEN_PUBLIC_KEY`)
- Uma URL para o serviço de autenticação Lambda (para `LAMBDA_AUTH_URL`)

1.  **Configurar Banco de Dados Local**:
    Você pode usar o `docker-compose.yml` para levantar um PostgreSQL e MailHog localmente:
    ```bash
    docker-compose up -d postgres mailhog
    ```
2.  **Configurar Variáveis de Ambiente**:
    Crie um arquivo `.env` na raiz do projeto ou defina as seguintes variáveis de ambiente:
    ```
    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/oficina_db
    SPRING_DATASOURCE_USERNAME=oficina_admin
    SPRING_DATASOURCE_PASSWORD=oficina_password
    API_SECURITY_TOKEN_PUBLIC_KEY="-----BEGIN PUBLIC KEY-----\n...\n-----END PUBLIC KEY-----"
    LAMBDA_AUTH_URL=http://localhost:8080 # Ou a URL do seu API Gateway da Lambda Auth
    NEW_RELIC_LICENSE_KEY=YOUR_NEW_RELIC_LICENSE_KEY # Opcional, para testar APM localmente
    ```
    **Nota**: A `API_SECURITY_TOKEN_PUBLIC_KEY` deve ser a chave pública RSA completa, incluindo os cabeçalhos `BEGIN/END` e com quebras de linha (`\n`). Esta chave deve corresponder à chave privada usada pela Lambda Auth.

3.  **Build da Aplicação**:
    ```bash
    ./mvnw clean install
    ```

4.  **Executar Migrações Flyway (se não usar Docker Compose)**:
    Se você não estiver usando o Docker Compose para o banco de dados, certifique-se de que o Flyway execute as migrações. Ao iniciar a aplicação, o Flyway fará isso automaticamente.

5.  **Executar a Aplicação (com New Relic Agent)**:
    Baixe o `newrelic.jar` e `newrelic.yml` do site do New Relic e coloque-os na pasta `target/`.
    ```bash
    java -javaagent:target/newrelic.jar -Dnewrelic.environment=development -jar target/oficinamecanica-0.0.1-SNAPSHOT.jar
    ```
    Ou, sem o agente New Relic:
    ```bash
    java -jar target/oficinamecanica-0.0.1-SNAPSHOT.jar
    ```

## 📋 Deploy (CI/CD com GitHub Actions)
Este repositório utiliza GitHub Actions para automação de CI/CD.
O workflow `main.yml` (localizado em `.github/workflows/main.yml`) executa as seguintes etapas:
1.  **`build-and-test`**:
    *   Compila o código Java com Maven.
    *   Executa testes unitários.
    *   Realiza um scan com SonarQube (se configurado).
2.  **`build-and-push-docker`**:
    *   Autentica no AWS ECR.
    *   Constrói a imagem Docker da aplicação e a envia para o ECR com a tag do SHA do commit.
3.  **`deploy-kubernetes`**:
    *   **Aprovação Manual**: Para deploys na branch `main` (ou ambiente `production`), é necessária uma aprovação manual (configurada via GitHub Environments).
    *   Configura o `kubectl` para o cluster EKS.
    *   Substitui placeholders nos manifestos Kubernetes (`k8s-deployment.yaml`, `k8s-configmap.yaml`, `k8s-secret.yaml`) com valores de Secrets e variáveis de ambiente.
    *   Aplica os manifestos no cluster Kubernetes (Deployment, Service, HPA).

**Configuração Necessária no GitHub:**
*   **Secrets**: Configure os seguintes GitHub Secrets no seu repositório:
    *   `AWS_ACCOUNT_ID`: O ID da sua conta AWS.
    *   `EKS_CLUSTER_NAME`: O nome do seu cluster EKS.
    *   `DB_HOST`: Host do seu RDS PostgreSQL (obtido do output do `fiap-tech-challenge-db-terraform`).
    *   `DB_USERNAME`: Usuário do banco de dados.
    *   `DB_PASSWORD`: Senha do banco de dados.
    *   `JWT_PUBLIC_KEY`: Sua chave pública RS256 para validação de JWTs.
    *   `NEW_RELIC_LICENSE_KEY`: Sua chave de licença do New Relic.
    *   `LAMBDA_AUTH_URL`: A URL do API Gateway do seu serviço de autenticação Lambda.
    *   `SONAR_TOKEN`: Token para autenticação no SonarQube (se usar).
    *   `SONAR_HOST_URL`: URL do seu servidor SonarQube (se usar).
*   **IAM Roles**: Crie os seguintes IAM Roles na AWS e configure-os para serem assumidos pelo GitHub Actions:
    *   `github-actions-app-ecr-role`: Com permissões para ECR.
    *   `github-actions-app-eks-deploy-role`: Com permissões para deploy no EKS (incluindo `eks:UpdateKubeconfig`, `sts:AssumeRole` para o role do EKS, e permissões para `kubectl apply`).
*   **Environments**: Crie um ambiente chamado `production` (ou o nome que você usou no workflow) nas configurações do seu repositório GitHub e adicione "Required reviewers" para aprovação manual.

## 🔗 Links Relacionados
- [Autenticação Lambda (fiap-tech-challenge-lambda-auth)](../fiap-tech-challenge-lambda-auth/README.md)
- [Infraestrutura Kubernetes (fiap-tech-challenge-k8s-terraform)](../fiap-tech-challenge-k8s-terraform/README.md)
- [Banco de Dados Terraform (fiap-tech-challenge-db-terraform)](../fiap-tech-challenge-db-terraform/README.md)
- [Documentação Geral da Fase 3](../../docs/Fase03/QuickStart-Fase3d-3e.md)
