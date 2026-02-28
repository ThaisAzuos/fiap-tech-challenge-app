# Diagrama Simplificado — PlantUML

```plantuml
@startuml
title Arquitetura Simplificada - Fase 2 Tech Challenge

actor Cliente as C

rectangle "Aplicação" {
  rectangle "Controllers (API)" as API
  rectangle "Serviços de Domínio" as Service
  rectangle "Repositórios (JPA)" as Repo
}

database "Banco de Dados" as DB

rectangle "Infraestrutura" {
  rectangle "Dockerfile / Imagem Docker" as Docker
  rectangle "Kubernetes Deployment" as K8s
  rectangle "Service + ConfigMaps/Secrets" as K8sSvc
  rectangle "Horizontal Pod Autoscaler" as HPA
}

rectangle "IaC" {
  rectangle "Terraform Scripts" as Terraform
}

rectangle "CI/CD" {
  rectangle "Pipeline (GitHub Actions/GitLab CI)" as Pipeline
}

C --> API
API --> Service
Service --> Repo
Repo --> DB

Docker --> K8s
K8s --> K8sSvc
K8s --> HPA
K8sSvc --> API

Terraform --> K8s
Terraform --> DB

Pipeline --> Docker
Pipeline --> K8s
Pipeline --> DB

@enduml


---

### Explicação rápida
- **Cliente** acessa a aplicação via controllers (APIs REST).  
- **Aplicação** organizada em camadas: controllers → serviços → repositórios → banco de dados.  
- **Infraestrutura**: aplicação containerizada (Docker), orquestrada em Kubernetes com Deployments, Services, ConfigMaps/Secrets e HPA.  
- **IaC**: Terraform provisiona cluster e banco de dados.  
- **CI/CD**: pipeline automatiza build, testes, criação de imagem Docker e deploy.  

---
