# ADR 003 — Segurança (OWASP + SonarQube)

**Status:** Aceita  
**Data:** 16 de Março de 2026  

---

## Resumo curto

Decidimos implementar conformidade com **OWASP Top 10** e ativar **análise estática com SonarQube** para proteger a aplicação contra vulnerabilidades comuns e garantir qualidade de código.

---

## Contexto

A aplicação está operacional mas sem proteções formais contra vulnerabilidades comuns. Requisitos de segurança:

- Proteger contra **SQL Injection, XSS, Command Injection**
- Validar e sanitizar **todos os inputs de usuário**
- Implementar **headers de segurança**
- Usar **parameterized queries** em todas as operações de banco
- Ativar **análise estática contínua**
- Seguir diretrizes do **OWASP Top 10 2024**
- Passar em **quality gates** do SonarQube

---

## Decisão

### 1. Proteção contra Injeção (SQL, XSS, Command)

**SQL Injection Prevention:**
- ✅ Spring Data JPA usa parameterized queries por padrão
- ✅ Nunca concatenar strings em queries
- ✅ Usar `@Query` ou method names quando possível

**XSS Prevention:**
- Sanitizar inputs em todos os DTOs
- Adicionar validators customizados
- Headers de segurança (Content-Security-Policy)

**Validação de Entrada:**
- Criar `@interface` customizadas para validação
- Aplicar em todos os DTOs
- Regras: tamanho, caracteres, formato

### 2. Configuração SonarQube

**Ativo em CI/CD com:**
- Quality Gates obrigatórios
- Blocking issues em build
- Relatórios automáticos

**Regras Enforcement:**
- Bugs: CRITICAL + MAJOR
- Vulnerabilities: CRITICAL
- Code Smells: MAJOR
- Cobertura: ≥ 40%

### 3. Security Headers

**Implementar em SecurityConfig:**
```
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
Strict-Transport-Security: max-age=31536000
Content-Security-Policy: default-src 'self'
X-XSS-Protection: 1; mode=block
```

### 4. Validadores Customizados

**Criar validadores para:**
- CPF (apenas números, sem caracteres)
- Placa de veículo (padrão brasileiro)
- Email (RFC 5322)
- Nomes (sem SQL injection)
- Números de telefone

---

## Implementação

### Arquivos para Criar/Modificar

```
src/main/java/com/grupo51/oficinamecanica/
├── comum/
│   ├── validation/
│   │   ├── SanitizationValidator.java       (novo)
│   │   ├── XssValidator.java                (novo)
│   │   ├── CpfValidator.java                (novo)
│   │   ├── PlacaVeiculoValidator.java       (novo)
│   │   └── EmailFormatValidator.java        (novo)
│   │
│   ├── config/
│   │   ├── SecurityConfig.java              (atualizar)
│   │   ├── SecurityHeadersFilter.java       (novo)
│   │   └── InputSanitizationFilter.java     (novo)
│   │
│   └── exception/
│       ├── SecurityException.java           (novo)
│       └── ValidationException.java         (novo)

pom.xml                                      (SonarQube ativo)
```

### Mudanças em Arquivos Existentes

**1. SecurityConfig.java** - Adicionar headers
**2. FuncionarioCadastroDTO.java** - Adicionar validadores
**3. ClienteDTO.java** - Adicionar validadores
**4. pom.xml** - SonarQube com quality gates

---

## Mitigações de Vulnerabilidade

### OWASP Top 10 - Mapeamento

| # | Vulnerabilidade | Mitigação |
|---|---|---|
| 1 | Broken Access Control | Spring Security + JWT + Roles ✅ |
| 2 | Cryptographic Failures | BCrypt + HMAC256 JWT ✅ |
| 3 | **Injection** | Parameterized JPA ✅ + Validators 🔨 |
| 4 | **Insecure Design** | DDD Rich Domain ✅ |
| 5 | **Security Misconfiguration** | Security Headers 🔨 |
| 6 | **Vulnerable Components** | Audit deps + updates 🔨 |
| 7 | Authentication Failures | JWT Well-implemented ✅ |
| 8 | **Data Integrity** | Validators + Snapshots ✅ |
| 9 | **Logging & Monitoring** | Spring Boot Actuator 🔨 |
| 10 | **SSRF** | Input validation 🔨 |

✅ = Já implementado  
🔨 = Implementar agora

---

## SonarQube Configuration

### pom.xml Properties
```xml
<sonarqube.enabled>true</sonarqube.enabled>
<sonar.projectKey>com.grupo51:oficinamecanica</sonar.projectKey>
<sonar.projectName>Oficina Mecânica API</sonar.projectName>
<sonar.projectVersion>${project.version}</sonar.projectVersion>
<sonar.sources>src/main/java</sonar.sources>
<sonar.tests>src/test/java</sonar.tests>
<sonar.java.binaries>target/classes</sonar.java.binaries>

<!-- Quality Gates -->
<sonar.qualitygate.wait>true</sonar.qualitygate.wait>

<!-- Security -->
<sonar.security.hotspots.reviewed.percentage>100</sonar.security.hotspots.reviewed.percentage>
<sonar.security.hotspots.critical.reviewed.percentage>100</sonar.security.hotspots.critical.reviewed.percentage>
```

### CI/CD Integration
```bash
mvn sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.login=admin123 \
  -Dsonar.qualitygate.wait=true
```

---

## Consequências

### Positivas
- 🟢 Proteção contra vulnerabilidades comuns
- 🟢 Conformidade com OWASP Top 10
- 🟢 Análise estática contínua
- 🟢 Qualidade de código garantida
- 🟢 Confiança para produção

### Negativas
- 🟡 Curva de aprendizado em validações customizadas
- 🟡 Setup inicial de SonarQube
- 🟡 Possível lentidão em builds com análise

### Mitigação de Negativos
- Documentar validadores com exemplos
- SonarQube em container local
- Cache de análise para builds rápidos

---

## Implementação - Roadmap

### Fase 1 (Hoje - 1 dia)
- [ ] Criar ADR 003 ✅
- [ ] Criar validadores básicos
- [ ] Atualizar SecurityConfig com headers
- [ ] SonarQube configurado em local

### Fase 2 (Dia 2-3)
- [ ] Integrar validadores em DTOs
- [ ] Criar testes para validadores
- [ ] SonarQube em CI/CD

### Fase 3 (Dia 4-5)
- [ ] Auditoria de todos os endpoints
- [ ] Remediar vulnerabilidades encontradas
- [ ] SonarQube quality gates bloqueando

---

## Métricas de Sucesso

| Métrica | Baseline | Target | Status |
|---------|----------|--------|--------|
| Vulnerabilidades críticas | 7 | 0 | 🔨 |
| Code smells | ? | < 50 | 🔨 |
| Cobertura de testes | 8% | 40% | 🔨 |
| Hotspots de segurança | ? | 0 critical | 🔨 |
| Build time | ? | < 2 min | 🔨 |

---

## Validação

### Antes (Sem ADR 003)
```
❌ SQL Injection possível
❌ XSS vulnerável
❌ Sem análise de código
❌ Sem validação formalizada
```

### Depois (Com ADR 003)
```
✅ SQL Injection prevenida (JPA + validators)
✅ XSS prevenida (sanitização + headers)
✅ SonarQube análise contínua
✅ Validação em todas as camadas
```

---


## Referências

- [OWASP Top 10 2024](https://owasp.org/www-project-top-ten/)
- [Spring Security Guide](https://spring.io/projects/spring-security)
- [SonarQube Best Practices](https://docs.sonarqube.org/)
- [OWASP Cheat Sheet](https://cheatsheetseries.owasp.org/)

---

## Próximos Passos

1. ✅ ADR 003 formalizada (HOJE)
2. 🔨 Implementar validadores (HOJE-AMANHÃ)
3. 🔨 Ativar SonarQube (AMANHÃ)
4. 🔨 Integrar com DTOs (DIA 3)
5. 🔨 Testes de validação (DIA 4-5)


