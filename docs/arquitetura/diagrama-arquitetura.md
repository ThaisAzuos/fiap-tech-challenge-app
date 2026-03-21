# Diagrama de Arquitetura — Fase 2 Tech Challenge

```mermaid
flowchart TD

    subgraph Usuario
        A[Cliente/Usuário] -->|HTTP/REST| B[API Gateway / Controllers]
    end

    subgraph Aplicacao
        B --> C[Serviços de Domínio]
        C --> D[Repositorios / JPA]
        D --> E[(Banco de Dados)]
    end

    subgraph Infraestrutura
        F[Dockerfile] --> G[Imagem Docker]
        G --> H[Deployment Kubernetes]
        H --> I[Service Kubernetes]
        I --> B
        H --> J[ConfigMaps/Secrets]
        H --> K[Horizontal Pod Autoscaler]
    end

    subgraph IaC
        L[Terraform Scripts] --> H
        L --> E
    end

    subgraph CI/CD
        M[Pipeline GitHub Actions/GitLab CI] --> G
        M --> H
        M --> E
    end

    subgraph Documentacao
        N[README.md] --> O[Desenho Arquitetura]
        N --> P[Instruções Execução Local/K8s]
        N --> Q[Collection APIs (Postman/Swagger)]
        N --> R[Vídeo Demonstrativo]
    end



---

### Explicação do diagrama
- **Usuário** acessa a aplicação via APIs REST.
- **Aplicação** segue princípios de Clean Code/Architecture, com separação em controllers, serviços e repositórios.
- **Infraestrutura**: aplicação containerizada (Docker), orquestrada em Kubernetes com Deployments, Services, ConfigMaps/Secrets e HPA para escalabilidade.
- **IaC**: scripts Terraform provisionam cluster Kubernetes e banco de dados.
- **CI/CD**: pipeline automatiza build, testes, criação de imagem Docker e deploy no cluster.
- **Documentação**: README.md atualizado com arquitetura, instruções, collection de APIs e vídeo demonstrativo.

---
