# 📋 PLANO DE AÇÃO - CONCLUSÃO DA FASE 1

**Data de Criação:** 16 de Março de 2026  
**Status Atual:** Fase 1 - 36% Completa  
**Prazo Recomendado:** 4-6 semanas  

---

## 🎯 OBJETIVO

Completar os requisitos faltantes da Fase 1 para garantir uma base sólida e sustentável antes de prosseguir para a Fase 2.

---

## 📊 DIAGNÓSTICO CONSOLIDADO

| Aspecto | Status | % | Prioridade |
|--------|--------|---|-----------|
| **Autenticação (ADR 001)** | ✅ Completo | 100% | ✅ OK |
| **Arquitetura (ADR 002)** | ⚠️ Parcial | 50% | 🟠 Alto |
| **Segurança (ADR 003)** | ❌ Crítico | 0% | 🔴 Crítico |
| **Documentação (ADR 004)** | ⚠️ Parcial | 40% | 🟠 Alto |
| **Testes** | ❌ Crítico | 8% | 🔴 Crítico |
| **E-mail** | ⚠️ Parcial | 50% | 🟠 Alto |

---

## 🔴 SPRINT 1 (Semana 1-2) - CRÍTICO

### Objetivo
Implementar segurança e criar base de testes

### Atividades

#### 1.1 ADR 003 - Segurança (OWASP + SonarQube)
**Responsável:** Arquiteto/Lead  
**Tempo:** 4 dias  
**Entregáveis:**
- Documento `ADR 003 — Segurança (OWASP + SonarQube).md`
- Guia de validação de entrada
- Checklist de vulnerabilidades

**Tarefas:**
```markdown
[ ] Criar ADR 003 formalizando:
    - SQL Injection prevention (parameterized queries)
    - XSS prevention (output encoding)
    - CSRF protection policy
    - Input validation rules
    - Security headers (X-Frame-Options, CSP, etc)

[ ] Implementar no código:
    - Input sanitization em DTOs críticos
    - Security headers em SecurityConfig
    - Validação customizada em validators

[ ] SonarQube:
    - Ativar análise em CI/CD
    - Criar quality gates
    - Documentar como rodar

[ ] Testes:
    - Testes de segurança básicos
    - Validação de entrada
```

**Arquivos para criar/modificar:**
```
src/main/java/com/grupo51/oficinamecanica/
├── comum/
│   ├── config/SecurityConfig.java        (adicionar headers)
│   ├── validation/                        (criar validadores)
│   │   ├── SanitizationValidator.java    (novo)
│   │   ├── SqlInjectionValidator.java    (novo)
│   │   └── XssValidator.java             (novo)
│   └── exception/
│       └── SecurityException.java         (novo)

ADRs/
└── ADR 003 — Segurança (OWASP + SonarQube).md (novo)
```

---

#### 1.2 Cobertura de Testes - Target 40%
**Responsável:** QA/Desenvolvedor  
**Tempo:** 6 dias  
**Entregáveis:**
- Mínimo 40% de cobertura
- Testes para domínio atendimento
- Testes de validação

**Tarefas:**
```markdown
[ ] Testes de Domínio (OrdemServico)
    [ ] Transições de status válidas
    [ ] Rejeição de transições inválidas
    [ ] Cálculo de valor
    [ ] Snapshot de preços

[ ] Testes de Aplicação (AtendimentoService)
    [ ] Abertura de OS
    [ ] Adição de itens
    [ ] Atualização de status
    [ ] Rejeição de operações inválidas

[ ] Testes de Validação
    [ ] Validação de entrada em DTOs
    [ ] Mensagens de erro
    [ ] Edge cases

[ ] Testes de Segurança
    [ ] Autenticação ausente → 401
    [ ] Autorização inválida → 403
    [ ] Input malicioso → 400 ou sanitizado
```

**Arquivos para criar:**
```
src/test/java/com/grupo51/oficinamecanica/
├── atendimento/
│   ├── domain/model/
│   │   └── OrdemServicoTest.java         (expandir)
│   ├── application/usecase/
│   │   └── AtendimentoServiceTest.java   (expandir)
│   └── infrastructure/repository/
│       └── OrdemServicoRepositoryTest.java (expandir)

├── cadastro/
│   ├── model/
│   │   ├── ClienteTest.java             (novo)
│   │   ├── FuncionarioTest.java         (novo)
│   │   └── VeiculoTest.java             (novo)
│   └── service/
│       └── CadastroServiceTest.java     (novo)

├── seguranca/
│   └── service/
│       ├── JwtTokenServiceTest.java     (novo)
│       └── LoginControllerTest.java     (novo)

└── comum/
    └── validation/
        ├── SanitizationValidatorTest.java (novo)
        └── XssValidatorTest.java         (novo)
```

**Métricas de Sucesso:**
- JaCoCo report ≥ 40%
- Testes passando: 100%
- Build sem warnings

---

### Acceptance Criteria
- ✅ ADR 003 criada e revisada
- ✅ JaCoCo > 40% cobertura
- ✅ Todos os testes passando
- ✅ Sem vulnerabilidades críticas no SonarQube

---

## 🟠 SPRINT 2 (Semana 3) - ALTO

### Objetivo
Implementar EmailService e documentar decisões

### Atividades

#### 2.1 Implementar EmailService
**Responsável:** Desenvolvedor Backend  
**Tempo:** 3 dias  
**Entregáveis:**
- EmailService funcional
- Templates de e-mail
- Integração com notificações

**Tarefas:**
```markdown
[ ] Criar EmailService
    [ ] Interface EmailService
    [ ] Implementação com Spring Mail
    [ ] Tratamento de erros
    [ ] Retry logic

[ ] Templates
    [ ] OS Aberta
    [ ] OS em Diagnóstico
    [ ] OS Aguardando Aprovação
    [ ] OS em Execução
    [ ] OS Concluída
    [ ] OS Entregue

[ ] Integração
    [ ] Trigger na abertura de OS
    [ ] Trigger em mudança de status
    [ ] Envio assíncrono (se possível)

[ ] Testes
    [ ] Teste de envio com MailHog
    [ ] Teste de template rendering
    [ ] Teste de tratamento de erro
```

**Arquivos para criar:**
```
src/main/java/com/grupo51/oficinamecanica/
├── comum/
│   └── email/
│       ├── EmailService.java            (novo - interface)
│       ├── EmailServiceImpl.java         (novo - implementação)
│       ├── template/
│       │   ├── EmailTemplate.java       (novo - enum)
│       │   └── templates/
│       │       ├── os-aberta.html       (novo)
│       │       ├── os-diagnostico.html  (novo)
│       │       ├── os-aprovacao.html    (novo)
│       │       ├── os-execucao.html     (novo)
│       │       ├── os-concluida.html    (novo)
│       │       └── os-entregue.html     (novo)
│       └── dto/
│           └── EmailDto.java            (novo)

src/test/java/com/grupo51/oficinamecanica/
└── comum/email/
    ├── EmailServiceTest.java            (novo)
    └── EmailTemplateTest.java           (novo)
```

**Configuração:**
```yaml
# application.properties
spring.mail.host=mailhog
spring.mail.port=1025
spring.mail.username=test
spring.mail.password=test
app.mail.from=noreply@oficinamecanica.com
```

---

#### 2.2 ADR 004 - Documentação
**Responsável:** Arquiteto/Tech Writer  
**Tempo:** 2 dias  
**Entregáveis:**
- ADR 004 formalizada
- Guia de contribuição
- Glossário de domínio

**Tarefas:**
```markdown
[ ] Criar ADR 004 — Documentação de Arquitetura
    [ ] Decisão de manter ADRs
    [ ] Padrão para novos ADRs
    [ ] Estrutura de documentação

[ ] Criar CONTRIBUTING.md
    [ ] Como setup local
    [ ] Padrões de código
    [ ] Processo de PR
    [ ] Checklist de segurança

[ ] Criar GLOSSARIO.md
    [ ] Termos de domínio
    [ ] Entidades principais
    [ ] Fluxos de negócio

[ ] Atualizar README.md
    [ ] Adicionar link para ADRs
    [ ] Adicionar link para CONTRIBUTING
    [ ] Adicionar diagrama de camadas
```

**Arquivos para criar:**
```
ADRs/
└── ADR 004 — Documentação de Arquitetura.md (novo)

Documentação/
├── CONTRIBUTING.md                       (novo)
├── GLOSSARIO.md                          (novo)
├── ARQUITETURA.md                        (novo)
└── FLUXOS_NEGOCIO.md                     (novo)
```

---

#### 2.3 Expandir Clean Architecture
**Responsável:** Desenvolvedor  
**Tempo:** 3 dias  
**Entregáveis:**
- Cadastro refatorado (40%)
- Base para outros módulos

**Tarefas:**
```markdown
[ ] Refatorar Cadastro (Cliente, Funcionário, Veículo)
    [ ] Mover lógica para domain/
    [ ] Criar interfaces em application/
    [ ] Reorganizar infrastructure/
    [ ] Adicionar testes

[ ] Manter estrutura para:
    [ ] Estoque
    [ ] Agendamento
    [ ] Segurança
```

---

### Acceptance Criteria
- ✅ EmailService enviando e-mails via MailHog
- ✅ ADR 004 formalizada
- ✅ CONTRIBUTING.md e GLOSSARIO.md criados
- ✅ Cadastro parcialmente refatorado

---

## 🟡 SPRINT 3 (Semana 4-5) - MÉDIO

### Objetivo
Completar arquitetura e aumentar cobertura de testes

### Atividades

#### 3.1 Clean Architecture - Conclusão
**Responsável:** Desenvolvedor  
**Tempo:** 4 dias  
**Entregáveis:**
- Todos os módulos com Clean Architecture

**Tarefas:**
```markdown
[ ] Refatorar Estoque
[ ] Refatorar Agendamento  
[ ] Refatorar Segurança (se aplicável)
[ ] Testes para cada módulo refatorado
```

---

#### 3.2 Aumentar Cobertura para 60%
**Responsável:** QA/Desenvolvedor  
**Tempo:** 4 dias  
**Entregáveis:**
- Cobertura JaCoCo ≥ 60%

**Tarefas:**
```markdown
[ ] Testes para Cadastro (20%)
[ ] Testes para Estoque (15%)
[ ] Testes para Agendamento (10%)
[ ] Testes de integração API (15%)
```

---

#### 3.3 BDD para Fluxos Críticos
**Responsável:** QA  
**Tempo:** 3 dias  
**Entregáveis:**
- Scenarios Gherkin para OS

**Tarefas:**
```markdown
[ ] Criar feature files
    [ ] Abertura de OS
    [ ] Mudanças de status
    [ ] Envio de e-mails

[ ] Implementar step definitions
[ ] Integrar em CI/CD
```

**Exemplo:**
```gherkin
Feature: Gerenciar Ordem de Serviço

Scenario: Abrir nova ordem de serviço
  Given um cliente cadastrado
  And um veículo associado
  When abro uma nova ordem de serviço
  Then a OS deve ser criada com status ABERTA
  And um e-mail de notificação deve ser enviado

Scenario: Atualizar status de OS
  Given uma OS aberta
  When atualizo o status para EM_DIAGNOSTICO
  Then o status deve ser alterado
  And um e-mail de notificação deve ser enviado
```

---

### Acceptance Criteria
- ✅ Todos os módulos com Clean Architecture
- ✅ Cobertura ≥ 60%
- ✅ Scenarios BDD implementados e passando

---

## 🟢 SPRINT 4 (Semana 6) - VALIDAÇÃO

### Objetivo
Validar e finalizar Fase 1

### Atividades

#### 4.1 Verificação Final
**Responsável:** Tech Lead  
**Tempo:** 2 dias  

**Checklist:**
```markdown
[ ] SonarQube sem bloqueadores críticos
[ ] Todos os testes passando
[ ] Cobertura ≥ 60%
[ ] ADRs 001-004 completadas
[ ] Documentação atualizada
[ ] EmailService funcional
[ ] Docker/K8s testados
[ ] OWASP Top 10 validado
```

---

#### 4.2 Ajustes e Correções
**Responsável:** Toda equipe  
**Tempo:** 2 dias  

---

#### 4.3 Demo e Retrospectiva
**Responsável:** Product Owner + Tech Lead  
**Tempo:** 1 dia  

---

## 📊 RECURSOS NECESSÁRIOS

### Equipe
- 1 Arquiteto/Tech Lead (20h/sprint)
- 2 Desenvolvedores Backend (40h/sprint)
- 1 QA/Tester (25h/sprint)
- 1 Tech Writer (10h/sprint)

**Total:** ~4.5 FTE / sprint

### Ferramentas
- ✅ IDE (JetBrains IntelliJ)
- ✅ Maven
- ✅ Docker & Docker Compose
- ✅ Git & GitHub
- ✅ SonarQube Community
- ✅ JUnit 5
- ✅ Mockito
- ✅ Cucumber (BDD)

---

## 📈 MÉTRICAS DE SUCESSO

### Sprint 1
- [ ] SonarQube ativo e sem bloqueadores
- [ ] Cobertura ≥ 40%
- [ ] ADR 003 aprovada

### Sprint 2
- [ ] EmailService enviando e-mails
- [ ] ADR 004 aprovada
- [ ] 2 módulos refatorados (40%)

### Sprint 3
- [ ] Cobertura ≥ 60%
- [ ] Todos os módulos com Clean Architecture
- [ ] BDD scenarios implementados

### Sprint 4
- [ ] Cobertura ≥ 60%
- [ ] Zero bloqueadores críticos
- [ ] Fase 1 = 100% completa

---

## ⚠️ RISCOS E MITIGAÇÕES

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|--------|-----------|
| Cobertura de testes não atinge meta | Média | Alto | Definir dono e deadline claro |
| Refatoração quebra funcionalidades | Média | Alto | Testes automatizados primeiro |
| SonarQube encontra muitos problemas | Alta | Médio | Começar análise logo |
| Falta de tempo | Média | Alto | Priorizar críticos, adiar médios |
| Mudanças de requisito | Baixa | Médio | Comunicação com PO |

---

## 📞 COMUNICAÇÃO

### Status Report
- **Frequência:** Semanal (toda 6ª-feira)
- **Audiência:** Tech Lead, PO, Stakeholders
- **Formato:** 
  - Progresso vs. Meta
  - Bloqueadores
  - Próximos passos
  - Métricas

### Daily Standup
- **Frequência:** Diária (9h)
- **Participantes:** Equipe dev + QA + Arquiteto
- **Duração:** 15 min

### Reunião de Planejamento
- **Frequência:** Início de cada sprint
- **Participantes:** Toda equipe
- **Duração:** 1-2h

---

## ✅ DEFINIÇÃO DE PRONTO (DoD)

### Feature
- [ ] Código revisado e aprovado (2 pessoas mínimo)
- [ ] Testes unitários com cobertura ≥ 80%
- [ ] Testes de integração (se aplicável)
- [ ] Sem bloqueadores no SonarQube
- [ ] Documentação atualizada
- [ ] Funciona localmente e em staging

### Sprint
- [ ] Todos os itens marcados como pronto
- [ ] Cobertura de testes atendida
- [ ] Review realizada
- [ ] Retrospectiva feita
- [ ] Demo para stakeholders

---

## 📅 CRONOGRAMA

```
├─ Sprint 1 (Mar 17 - Mar 30)
│  ├─ Seg-Qua: ADR 003 + segurança
│  ├─ Qui-Sex: Cobertura de testes (40%)
│  └─ Checkpoint: Mar 30
│
├─ Sprint 2 (Abr 01 - Abr 13)
│  ├─ Seg-Ter: EmailService
│  ├─ Qua-Qui: ADR 004 + documentação
│  ├─ Sex: Clean Architecture início
│  └─ Checkpoint: Abr 13
│
├─ Sprint 3 (Abr 14 - Abr 27)
│  ├─ Seg-Ter: Clean Architecture completo
│  ├─ Qua-Qui: Cobertura 60%
│  ├─ Sex: BDD scenarios
│  └─ Checkpoint: Abr 27
│
└─ Sprint 4 (Abr 28 - Abr 30)
   ├─ Seg: Verificação final
   ├─ Ter: Ajustes
   ├─ Qua: Demo + Retrospectiva
   └─ ✅ FASE 1 COMPLETA!
```

---

## 📝 CONCLUSÃO

Este plano fornece um roadmap claro para completar a Fase 1 em 4 semanas com foco em:

1. **Segurança** (ADR 003) - Implementar proteções OWASP
2. **Testes** - Aumentar cobertura para 60%
3. **E-mail** - Implementar notificações
4. **Arquitetura** - Aplicar Clean Architecture em todos os módulos
5. **Documentação** (ADR 004) - Formalizar decisões

**Sucesso depende de:**
- ✅ Comprometimento da equipe
- ✅ Comunicação clara
- ✅ Foco nas prioridades
- ✅ Qualidade acima de quantidade

**Próximo Passo:** Aprovar este plano em reunião de sprint planning e começar Sprint 1 na segunda-feira.

---

**Aprovação:**
- Tech Lead: _________________ Data: _______
- Product Owner: _________________ Data: _______
- Arquiteto: _________________ Data: _______

