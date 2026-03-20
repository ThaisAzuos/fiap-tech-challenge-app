# Comparativo de Arquitetura — Fase 1 vs Fase 2

```mermaid
flowchart LR

subgraph Fase1["Fase 1 - Base"]
    A1[DDD: Entidades e Serviços]
    B1[Controllers / APIs iniciais]
    C1[Repositórios JPA]
    D1[(Banco de Dados)]
    E1[Documentação parcial]
    F1[Segurança básica]
    G1[Testes insuficientes]
    
    A1 --> B1 --> C1 --> D1
    A1 --> E1
    A1 --> F1
    A1 --> G1
end

subgraph Fase2["Fase 2 - Evolução"]
    A2[Clean Code + Arquitetura Hexagonal]
    B2[APIs completas (OS, Listagem, etc.)]
    C2[Testes automatizados (unitários/integrados)]
    D2[Segurança reforçada + JWT]
    E2[Dockerfile + Compose]
    F2[Kubernetes (Deployments, Services, HPA)]
    G2[Terraform (IaC)]
    H2[Pipeline CI/CD]
    I2[Documentação completa + ADRs]
    
    A2 --> B2 --> C2 --> D2
    A2 --> E2 --> F2 --> G2 --> H2
    A2 --> I2
end

Fase1 --> Fase2

