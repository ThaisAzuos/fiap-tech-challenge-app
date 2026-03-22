# ADRs — Evolução da Aplicação (Fase 2 Tech Challenge)

Este documento organiza as decisões arquiteturais (ADRs) em etapas de evolução, separadas por contexto: **Código**, **Infraestrutura**, **Automação (CI/CD)** e **Documentação/Entrega**.

---

## ADR 001 — Refatoração de Código e Qualidade

**Status:** Aceita  
**Contexto:** Evolução da aplicação para maior clareza, coesão e sustentabilidade.  
**Decisão:**
- Aplicar **Clean Code** (nomes claros, simplicidade, coesão).
- Iniciar adaptação para **Clean Architecture/Hexagonal** (separação de camadas).
- Criar/alterar APIs:
    - **Abertura de Ordem de Serviço (OS)**.
    - **Listagem de OS** com ordenação por status e exclusão lógica de finalizadas/entregues.
- Implementar **testes automatizados** (unitários e integração) para fluxos críticos.

**Consequências:**
- Código mais sustentável e legível.
- APIs alinhadas às regras de negócio.
- Garantia de qualidade via testes.

---

## ADR 002 — Containerização e Orquestração

**Status:** Aceita  
**Contexto:** Preparar aplicação para execução em ambientes escaláveis.  
**Decisão:**
- Criar/atualizar **Dockerfile** para build da aplicação.
- Adicionar **docker-compose** para desenvolvimento local.
- Criar manifestos **Kubernetes (YAML)**:
    - Deployments.
    - Services.
    - ConfigMaps e Secrets para variáveis sensíveis.
    - Horizontal Pod Autoscaler (HPA) para escalabilidade automática.

**Consequências:**
- Aplicação containerizada e pronta para orquestração.
- Escalabilidade dinâmica em horários de pico.
- Separação clara de configuração e segredos.

---

## ADR 003 — Infraestrutura como Código (IaC)

**Status:** Aceita  
**Contexto:** Provisionamento automatizado e reprodutível.  
**Decisão:**
- Criar scripts **Terraform** para provisionamento de:
    - Cluster Kubernetes (local ou cloud).
    - Banco de dados.
- Documentar recursos criados e instruções de aplicação.

**Consequências:**
- Infraestrutura versionada e auditável.
- Redução de riscos operacionais.
- Facilidade de replicação em diferentes ambientes.

---

## ADR 004 — Integração e Entrega Contínua (CI/CD)

**Status:** Aceita  
**Contexto:** Automatizar build, testes e deploy.  
**Decisão:**
- Configurar pipeline CI/CD (GitHub Actions ou GitLab CI):
    - Build da aplicação.
    - Execução dos testes automatizados.
    - Build da imagem Docker.
    - Deploy no cluster Kubernetes.
    - Deploy do banco de dados.
    - Aplicação dos manifestos YAML.

**Consequências:**
- Deploy contínuo e confiável.
- Redução de falhas humanas.
- Feedback rápido sobre qualidade e estabilidade.

---

## ADR 005 — Documentação e Entregáveis

**Status:** Aceita  
**Contexto:** Garantir clareza e rastreabilidade da solução entregue.  
**Decisão:**
- Atualizar **README.md** com:
    - Descrição da solução e objetivos da fase.
    - Desenho da arquitetura proposta.
    - Componentes da aplicação e infraestrutura.
    - Fluxo de deploy.
    - Instruções para execução local e deploy em Kubernetes/Terraform.
- Disponibilizar:
    - Collection completa das APIs (Postman/Swagger).
    - Vídeo demonstrativo (até 15 min) mostrando deploy, CI/CD e consumo das APIs.
- Entregar PDF com links para repositório, desenho da arquitetura e vídeo.

**Consequências:**
- Transparência e rastreabilidade da solução.
- Facilidade de onboarding para novos desenvolvedores.
- Evidência clara de cumprimento dos requisitos da fase.

---

# Checklist de Evolução

1. **Código**
    - Refatorar para Clean Code.
    - Implementar APIs de OS.
    - Criar testes automatizados.

2. **Infraestrutura**
    - Atualizar Dockerfile.
    - Criar docker-compose.
    - Escrever manifestos Kubernetes.

3. **IaC**
    - Criar scripts Terraform.
    - Documentar recursos.

4. **CI/CD**
    - Configurar pipeline.
    - Automatizar build, testes e deploy.

5. **Documentação/Entrega**
    - Atualizar README.md.
    - Criar collection de APIs.
    - Gravar vídeo demonstrativo.
    - Entregar PDF com links e arquitetura.  
