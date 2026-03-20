# 📅 Plano de Execução – Tech Challenge (10 dias)

Este documento descreve o plano de trabalho para evolução da aplicação da Fase 1, contemplando refatoração, infraestrutura e automação, conforme requisitos obrigatórios.

---

## 🎯 Objetivos
- Refatorar código aplicando **Clean Code** e **Clean Architecture/Hexagonal**.
- Implementar e ajustar APIs obrigatórias.
- Criar testes automatizados para fluxos críticos.
- Containerizar aplicação com Docker.
- Orquestrar com Kubernetes (Deployments, Services, ConfigMaps, Secrets, HPA).
- Provisionar infraestrutura com Terraform.
- Configurar pipeline CI/CD (build, testes, deploy).
- Documentar solução e entregar vídeo demonstrativo.

---

## 📌 Cronograma de 10 dias

### **Dia 1 – Organização e Planejamento**
- Revisar código da Fase 1.
- Definir arquitetura alvo.
- Criar branch específica no Git.
- Mapear APIs obrigatórias e fluxos críticos.

### **Dia 2 – Refatoração inicial**
- Aplicar princípios de Clean Code.
- Estruturar camadas conforme arquitetura escolhida.
- Documentar decisões no README.

### **Dia 3 – Implementação das APIs**
- Criar/alterar endpoints:
    - Abertura de OS.
    - Consulta de status.
    - Aprovação de orçamento.
    - Listagem de OS com ordenação e exclusão lógica.

### **Dia 4 – Testes Automatizados**
- Implementar testes unitários.
- Configurar testes de integração básicos.
- Garantir cobertura mínima (>60%).

### **Dia 5 – Containerização**
- Revisar e atualizar Dockerfile.
- Criar docker-compose para ambiente local.
- Testar execução em containers.

### **Dia 6 – Kubernetes**
- Criar manifestos YAML:
    - Deployments, Services.
    - ConfigMaps e Secrets.
    - Horizontal Pod Autoscaler (HPA).
- Testar deploy em cluster local (kind/minikube).

### **Dia 7 – Infraestrutura como Código (IaC)**
- Criar scripts Terraform para:
    - Cluster Kubernetes.
    - Banco de dados.
- Documentar recursos e instruções de aplicação.

### **Dia 8 – CI/CD**
- Configurar pipeline (GitHub Actions/GitLab CI):
    - Build da aplicação.
    - Execução de testes.
    - Build da imagem Docker.
    - Deploy no cluster Kubernetes.
    - Deploy do banco de dados.
    - Aplicação dos manifestos YAML.

### **Dia 9 – Documentação e APIs**
- Atualizar README com:
    - Descrição da solução.
    - Desenho da arquitetura.
    - Instruções de execução local, deploy e IaC.
- Criar collection Postman/Swagger para APIs.

### **Dia 10 – Demonstração e Entregáveis**
- Gravar vídeo (até 15 min) mostrando:
    - Deploy da aplicação.
    - Execução da pipeline CI/CD.
    - Consumo das APIs.
    - Escalabilidade automática.
- Publicar vídeo no YouTube/Vimeo.
- Gerar PDF com links (GitHub, vídeo, desenho da arquitetura).

---

## 📂 Entregáveis
- Repositório Git atualizado (código refatorado, Dockerfile, docker-compose, manifestos Kubernetes, scripts Terraform, pipeline CI/CD).
- README.md completo.
- Collection de APIs (Postman/Swagger).
- Vídeo demonstrativo (YouTube/Vimeo).
- PDF com links e desenho da arquitetura.

---

## ✅ Status de Acompanhamento
- [x] Dia 1 – Organização
- [x] Dia 2 – Refatoração
- [x] Dia 3 – APIs
- [x] Dia 4 – Testes
- [x] Dia 5 – Docker
- [x] Dia 6 – Kubernetes
- [x] Dia 7 – Terraform
- [x] Dia 8 – CI/CD
- [x] Dia 9 – Documentação
- [ ] Dia 10 – Demonstração
