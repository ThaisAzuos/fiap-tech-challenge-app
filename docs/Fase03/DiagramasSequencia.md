# Diagramas ER e Sequência - Fase 3

## 📊 Diagrama Entidade-Relacionamento (ER)

### Visão Relacional Completa

```mermaid
erDiagram
    CLIENTES ||--o{ VEICULOS : possui
    CLIENTES ||--o{ ENDERECO : reside
    VEICULOS ||--o{ ORDENS_SERVICO : "e alvo"
    ORDENS_SERVICO ||--o{ ITENS_OS : contém
    PECAS ||--o{ ITENS_OS : "usada em"
    SERVICOS ||--o{ ITENS_OS : "realizado em"
    ORDENS_SERVICO ||--o{ APROVACAO_ORCAMENTO : "precisa de"
    ORDENS_SERVICO ||--o{ NOTIFICATIONS : gera
    CLIENTES ||--o{ FUNCIONARIOS : interagem
    FUNCIONARIOS ||--o{ ESPECIALIDADE : possuem

    CLIENTES {
        uuid id PK
        string cpf UK
        string nome
        string email
        string telefone
        timestamp created_at
        timestamp updated_at
    }

    ENDERECO {
        uuid id PK
        uuid cliente_id FK
        string rua
        string numero
        string complemento
        string cidade
        string estado
        string cep
    }

    VEICULOS {
        uuid id PK
        uuid cliente_id FK
        string placa UK
        string marca
        string modelo
        int ano
        string cor
        timestamp created_at
    }

    ORDENS_SERVICO {
        uuid id PK
        uuid veiculo_id FK
        string status
        string descricao
        decimal valor_total
        timestamp data_abertura
        timestamp data_fechamento
        timestamp created_at
        timestamp updated_at
    }

    ITENS_OS {
        uuid id PK
        uuid ordem_id FK
        uuid peca_id FK
        uuid servico_id FK
        int quantidade
        decimal preco_unitario_snapshot
        timestamp created_at
    }

    PECAS {
        uuid id PK
        string nome
        string descricao
        decimal valor_atual
        int quantidade_estoque
        timestamp created_at
    }

    SERVICOS {
        uuid id PK
        string nome
        string descricao
        decimal valor_atual
        int tempo_estimado_minutos
        timestamp created_at
    }

    APROVACAO_ORCAMENTO {
        uuid id PK
        uuid ordem_id FK
        string status
        timestamp data_solicitacao
        timestamp data_aprovacao
        string justificativa_recusao
    }

    NOTIFICATIONS {
        uuid id PK
        uuid ordem_id FK
        string tipo
        string destinatario_email
        string status_envio
        timestamp created_at
    }

    FUNCIONARIOS {
        uuid id PK
        string nome
        string cpf UK
        string email
        string telefone
        string cargo
        timestamp data_admissao
    }

    ESPECIALIDADE {
        uuid id PK
        string nome
        string descricao
    }
```

### Descrição dos Relacionamentos

| Relacionamento | Tipo | Descrição |
|---|---|---|
| **CLIENTES → VEICULOS** | 1:N | Um cliente pode possuir vários veículos |
| **CLIENTES → ENDERECO** | 1:1 | Um cliente tem um endereço principal |
| **VEICULOS → ORDENS_SERVICO** | 1:N | Um veículo gera múltiplas ordens de serviço |
| **ORDENS_SERVICO → ITENS_OS** | 1:N | Uma OS tem múltiplos itens (peças + serviços) |
| **PECAS → ITENS_OS** | N:N | Uma peça pode ser usada em múltiplas OS |
| **SERVICOS → ITENS_OS** | N:N | Um serviço pode ser realizado em múltiplas OS |
| **ORDENS_SERVICO → APROVACAO_ORCAMENTO** | 1:1 | Uma OS tem um orçamento + aprovação |
| **ORDENS_SERVICO → NOTIFICACTIONS** | 1:N | Uma OS gera multiple notificações |

### Constraints & Índices

```sql
-- Unique constraints
ALTER TABLE clientes ADD CONSTRAINT uk_clientes_cpf UNIQUE(cpf);
ALTER TABLE veiculos ADD CONSTRAINT uk_veiculos_placa UNIQUE(placa);
ALTER TABLE funcionarios ADD CONSTRAINT uk_funcionarios_cpf UNIQUE(cpf);

-- Indexes para performance
CREATE INDEX idx_ordens_veiculo ON ordens_servico(veiculo_id);
CREATE INDEX idx_ordens_status ON ordens_servico(status);
CREATE INDEX idx_ordens_data_abertura ON ordens_servico(data_abertura DESC);
CREATE INDEX idx_itens_ordem ON itens_os(ordem_id);
CREATE INDEX idx_notificacoes_ordem ON notificacoes(ordem_id);
CREATE INDEX idx_veiculos_cliente ON veiculos(cliente_id);

-- JSON index se usar campo JSONB
CREATE INDEX idx_ordens_metadados ON ordens_servico USING GIN (metadados);
```

---

## 🔄 Diagramas de Sequência

### 1. Fluxo de Autenticação (Lambda + API Gateway)

```mermaid
sequenceDiagram
    participant U as User (Postman)
    participant AG as API Gateway<br/>(AWS)
    participant L as Lambda<br/>(auth)
    participant DB as PostgreSQL<br/>(RDS)
    participant KB as Secrets Mgr<br/>(AWS KMS)
    participant APP as Spring Boot<br/>App

    U->>AG: POST /authenticate<br/>{cpf: "12345678901"}
    AG->>L: Invoke Lambda
    activate L
    
    L->>DB: SELECT * FROM clientes<br/>WHERE cpf = ?
    DB->>L: cliente {id, cpf, nome, email, ativo}
    
    alt Cliente não encontrado ou inativo
        L->>L: Log: autenticacao_falhou
        L-->>AG: HTTP 401 Unauthorized<br/>{error: "CPF inválido"}
        AG-->>U: HTTP 401 {error}
    else Cliente válido
        L->>KB: GET /oficina/jwt-signing-key
        KB->>L: private_key (RSA)
        
        L->>L: Generate JWT<br/>RS256 signing<br/>expiry: 1h
        L->>L: Log: autenticacao_sucesso<br/>(correlationId)
        L-->>AG: HTTP 200<br/>{<br/>token: "eyJ...",<br/>expiry: 3600<br/>}
        AG-->>U: HTTP 200 {token}
        
        U->>APP: GET /api/v1/ordens-servico<br/>Header: Authorization: Bearer eyJ...
        APP->>APP: Validate JWT signature
        APP->>APP: Extract userId from JWT
        APP->>DB: SELECT * FROM ordens_servico<br/>WHERE status NOT IN (FINALIZADA, ENTREGUE)
        DB->>APP: [ordem1, ordem2, ...]
        APP-->>U: HTTP 200 {ordens}
    end
    
    deactivate L
```

**Timing esperado**:
- Cold start Lambda: ~3000ms (primeira chamada)
- Warm Lambda: ~200-300ms
- DB query: ~50ms
- JWT generation: ~10ms
- **Total P95**: ~300ms (depois aquecido)

---

### 2. Fluxo de Abertura de Ordem de Serviço

```mermaid
sequenceDiagram
    participant U as User<br/>(Cliente)
    participant APP as Spring Boot<br/>App
    participant DOM as Domain<br/>(Clean Arch)
    participant DB as PostgreSQL
    participant MH as MailHog<br/>(Email)
    participant NR as New Relic<br/>(Monitoring)

    U->>APP: POST /api/v1/ordens-servico<br/>{<br/>placa, descricao,<br/>servicos: [...],<br/>pecas: [...]<br/>}
    
    activate APP
    APP->>DOM: abrir(aberturaOSDTO)
    activate DOM
    
    DOM->>DOM: Validar placa
    DOM->>DOM: Validar descricao
    DOM->>DOM: Validar servicos[]
    DOM->>DOM: Validar pecas[]
    
    alt Validação falha
        DOM-->>APP: DomainException
        APP-->>U: HTTP 400 {error}
    else Validação OK
        DOM->>DB: SELECT veiculo WHERE placa = ?
        DB->>DOM: veiculo
        
        DOM->>DB: INSERT INTO ordens_servico (veiculo_id, status='RECEBIDA', ...)
        DOM->>DB: INSERT INTO itens_os (ordem_id, peca_id, preco_snapshot, ...)
        DOM->>DB: INSERT INTO itens_os (ordem_id, servico_id, preco_snapshot, ...)
        DB->>DOM: OrderId
        
        DOM->>DOM: Domain event: OrdemAberta
        DOM-->>APP: OrdemServico (domain)
        deactivate DOM
        
        APP->>MH: POST /api/emails<br/>{to: cliente.email, titulo, corpo}
        MH-->>APP: 202 Accepted
        
        APP->>NR: Track metric<br/>oficina.ordem.criada (1)
        NR-->>APP: accepted
        
        deactivate APP
        APP-->>U: HTTP 201<br/>{orderId, status, ...}
    end
```

**Timing esperado**:
- Validação domínio: ~10ms
- DB query veiculo: ~30ms
- DB insert OS + itens: ~40ms
- Email send: ~100ms (async)
- Total: ~250ms P95

---

### 3. Fluxo de Alteração de Status + Notificação

```mermaid
sequenceDiagram
    participant M as Mecanico<br/>(via UI)
    participant APP as Spring Boot<br/>App
    participant DB as PostgreSQL
    participant MH as MailHog<br/>(Email)
    participant NR as New Relic
    participant LOG as CloudWatch<br/>(Logs)

    M->>APP: PATCH /api/v1/ordens-servico/os-123<br/>{status: "EM_DIAGNOSTICO"}
    
    activate APP
    APP->>LOG: Info: status_update_started<br/>(correlationId, userId)
    
    APP->>DB: SELECT * FROM ordens_servico<br/>WHERE id = 'os-123'
    DB->>APP: ordem (status=RECEBIDA)
    
    APP->>APP: Validate state transition<br/>RECEBIDA → EM_DIAGNOSTICO (ok)
    
    APP->>DB: UPDATE ordens_servico<br/>SET status = 'EM_DIAGNOSTICO'<br/>WHERE id = 'os-123'
    
    APP->>APP: Load cliente via veiculo_id
    APP->>MH: POST /api/emails<br/>{to: cliente.email,<br/>title: "OS em diagnóstico",<br/>body: "..."}
    MH-->>APP: 202 Accepted (async)
    
    APP->>NR: Track:<br/>oficina.ordem.status_changed<br/>(from: RECEBIDA, to: EM_DIAGNOSTICO)
    
    APP->>NR: Track:<br/>oficina.ordem.tempo_status<br/>(valor: 45s, status: RECEBIDA)
    
    APP->>LOG: Info: status_update_completed<br/>(duration: 78ms, success: true)
    
    deactivate APP
    APP-->>M: HTTP 200 {ordem}
```

**SLA de notificação**: Email dentro de 5 minutos

---

### 4. Deployment to Kubernetes (CI/CD Pipeline)

```mermaid
sequenceDiagram
    participant GH as GitHub
    participant AC as GitHub Actions<br/>(CI/CD)
    participant SQ as SonarQube
    participant ECR as AWS ECR<br/>(Registry)
    participant K8s as Kubernetes<br/>Cluster
    participant NR as New Relic

    participant Dev as Developer

    Dev->>GH: git push origin feature/...
    GH->>AC: Trigger workflow (PR)
    
    activate AC
    AC->>AC: Maven compile
    AC->>AC: Maven test
    AC->>SQ: Coverage report
    SQ-->>AC: Quality gate (pass/fail)
    
    alt SonarQube fail
        AC-->>Dev: 🔴 Build failed
    else Pass
        AC->>AC: docker build
        AC->>ECR: docker push<br/>oficina-app:v1.3.0-a1b2c3d
        ECR-->>AC: pushed
        AC-->>AC: Create Release notes
        AC-->>GH: PR ready for review
    end
    deactivate AC

    Dev->>GH: Create PR → Code review
    GH->>GH: Branch protection check<br/>(2 reviewers required)
    
    alt Reviewers approve
        Dev->>GH: Merge to main
        GH->>AC: Trigger deploy workflow
        
        activate AC
        AC->>K8s: kubectl apply -f k8s-deployment.yaml<br/>(image: ECR/oficina-app:v1.3.0-a1b2c3d)
        K8s->>K8s: Rolling update<br/>(maxSurge: 1, maxUnavailable: 0)
        K8s-->>AC: Deployment rolled out (2 replicas)
        
        AC->>K8s: kubectl rollout status deployment/oficina-app
        K8s-->>AC: rollout successful
        
        AC->>NR: Report: Deployment done<br/>(version: v1.3.0, deployed_by: dev1)
        NR-->>AC: ✓ logged
        
        AC-->>AC: 🟢 Deploy successful
        deactivate AC
    else Review rejected
        Dev->>GH: Make changes
    end
```

**SLA de Deploy**:
- Build: < 5 minutos
- Test: < 3 minutos
- Image push: < 1 minuto
- K8s rollout: < 2 minutos
- **Total**: < 15 minutos

---

## 📐 Diagrama de Componentes (Fase 3)

```mermaid
graph TB
    subgraph "🌐 Client Layer"
        FE["Frontend\n(Postman/Browser)"]
    end

    subgraph "AWS Cloud"
        subgraph "API Layer"
            APIGW["API Gateway\n(Roteamento)"]
        end

        subgraph "Serverless Layer"
            LAMBDA["Lambda Function\n(Auth)\n- CPF validation\n- JWT generation"]
        end

        subgraph "Compute Layer"
            EKS["EKS Cluster\n(Kubernetes)"]
            subgraph "K8s Pods"
                APP["Spring Boot App\n(3 peças)"]
                MM["MailHog\n(Email)"]
            end
            HPA["HPA\n(CPU + Memory)"]
        end

        subgraph "Data Layer"
            RDS["RDS PostgreSQL\n(20GB)"]
            BACKUP["RDS Backups\n(Multi-AZ)"]
        end

        subgraph "Secrets & Security"
            SM["AWS Secrets Manager\n(JWT key, DB pwd)"]
            KMS["AWS KMS\n(Encryption)"]
        end

        subgraph "Monitoring & Logging"
            CW["CloudWatch\n(Logs)"]
            NR["New Relic\n(APM + Dashboard)"]
        end
    end

    subgraph "GitHub"
        REPO1["Repo: lambda-auth"]
        REPO2["Repo: k8s-terraform"]
        REPO3["Repo: db-terraform"]
        REPO4["Repo: app"]
        ACTIONS["GitHub Actions\n(CI/CD)"]
    end

    subgraph "ECR"
        REG["AWS ECR\n(Docker Images)"]
    end

    FE -->|POST /authenticate| APIGW
    APIGW -->|invoke| LAMBDA
    LAMBDA -->|query CPF| RDS
    LAMBDA -->|get key| SM
    LAMBDA -->|return JWT| APIGW
    APIGW -->|return token| FE

    FE -->|API calls + JWT| APIGW
    APIGW -->|route| APP

    APP -->|query/update| RDS
    APP -->|get secrets| SM
    APP -->|send email| MM
    APP -->|structured logs| CW
    APP -->|metrics/traces| NR

    HPA -->|monitor| APP
    HPA -->|scale| EKS

    KMS -->|encrypt| SM
    KMS -->|encrypt| RDS

    REPO1 -->|push| ACTIONS
    REPO2 -->|push| ACTIONS
    REPO3 -->|push| ACTIONS
    REPO4 -->|push| ACTIONS

    ACTIONS -->|build & push| REG
    ACTIONS -->|deploy| EKS
    ACTIONS -->|terraform apply| RDS

    NR -->|dashboard| FE
    CW -->|logs| NR

    style LAMBDA fill:#FF9900
    style EKS fill:#FF9900
    style RDS fill:#FF9900
    style NR fill:#146EB4
    style ACTIONS fill:#2088F0
```

---

## Checklist - Diagrama de Componentes

- ✅ Fluxo de autenticação (User → AW → Lambda → App)
- ✅ Isolamento de camadas (API → Serverless → Compute → Data)
- ✅ Segurança integrada (KMS, Secrets Manager)
- ✅ Observabilidade (New Relic, CloudWatch)
- ✅ CI/CD pipeline (GitHub → Actions → ECR → K8s)
- ✅ Alta disponibilidade (Multi-AZ RDS, HPA K8s)


