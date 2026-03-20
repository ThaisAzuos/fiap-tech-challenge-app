# ADRs — Fase 1 (Base do Projeto)

Este documento lista as decisões arquiteturais (ADRs) que deveriam ter sido implementadas na Fase 1, mas ficaram pendentes. A Fase 2 já está completa e evolui sobre esta base.

---

## ADR 001 — Aplicação de Clean Architecture / Hexagonal

**Status:** Pendente na Fase 1  
**Contexto:** A Fase 1 introduziu conceitos de DDD e arquitetura, mas não consolidou a separação clara de camadas.  
**Decisão:**  
- Implementar **Arquitetura Hexagonal** ou **Clean Architecture**.  
- Separar domínio, aplicação e infraestrutura em pacotes distintos.  
- Definir interfaces para repositórios e serviços externos.  

**Consequências:**  
- Maior desacoplamento e testabilidade.  
- Facilita evolução futura (já aplicada na Fase 2).  

---

## ADR 002 — Testes Automatizados (TDD/BDD)

**Status:** Pendente na Fase 1  
**Contexto:** A disciplina de Qualidade de Software previa uso de TDD/BDD, mas não houve cobertura suficiente nos fluxos críticos.  
**Decisão:**  
- Criar **testes unitários** para entidades e serviços.  
- Implementar **testes de integração** para APIs principais.  
- Adotar **BDD** para cenários de negócio relevantes.  

**Consequências:**  
- Garantia de qualidade desde a base.  
- Redução de regressões em evoluções posteriores.  

---

## ADR 003 — Segurança (OWASP + SonarQube)

**Status:** Pendente na Fase 1  
**Contexto:** A disciplina de Desenvolvimento Seguro previa mitigação de vulnerabilidades, mas não foi consolidada.  
**Decisão:**  
- Implementar validações contra **SQL Injection, XSS, Command Injection**.  
- Configurar análise estática com **SonarQube**.  
- Seguir diretrizes do **OWASP Top 10**.  

**Consequências:**  
- Base mais robusta contra ataques.  
- Conformidade com boas práticas de segurança.  

---

## ADR 004 — Documentação de Arquitetura

**Status:** Pendente na Fase 1  
**Contexto:** A disciplina de Documentação previa registro de decisões arquiteturais e fluxos técnicos, mas não foi formalizada.  
**Decisão:**  
- Criar **ADRs** para cada decisão tomada na Fase 1.  
- Documentar **fluxos técnicos** e **modelagem de domínio** (DDD, Event Storming).  
- Manter README inicial com visão da arquitetura.  

**Consequências:**  
- Clareza e rastreabilidade desde a base.  
- Facilita evolução e integração de novos membros.  

---

# Checklist de Implementação (Fase 1)

1. **Arquitetura**
   - Separar camadas (domínio, aplicação, infraestrutura).
   - Definir interfaces e adaptadores.

2. **Qualidade**
   - Criar testes unitários e de integração.
   - Adotar TDD/BDD nos fluxos críticos.

3. **Segurança**
   - Implementar validações contra vulnerabilidades comuns.
   - Configurar SonarQube e seguir OWASP Top 10.

4. **Documentação**
   - Formalizar ADRs da Fase 1.
   - Registrar modelagem de domínio (DDD, Event Storming).
   - Atualizar README com visão inicial da arquitetura.

---

