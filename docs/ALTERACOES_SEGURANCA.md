# Alterações de Autorização - SecurityConfig

## Resumo das Alterações

Foi realizada uma atualização no arquivo `SecurityConfig.java` para permitir que endpoints de cadastro funcionem com qualquer usuário autenticado, sem restrição de role específica.

## Mudanças Realizadas

### Arquivo: `src/main/java/com/grupo51/oficinamecanica/comum/config/SecurityConfig.java`

**Antes:**
```java
.authorizeHttpRequests(req -> {
    req.requestMatchers("/actuator/**").permitAll();
    req.requestMatchers(HttpMethod.POST, "/login").permitAll();

    req.requestMatchers("/api/v1/atendentes/**").hasRole("ATENDENTE");
    req.requestMatchers("/api/v1/gerentes/**").hasRole("GERENTE");
    req.requestMatchers("/api/v1/mecanicos/**").hasRole("MECANICO");

    req.anyRequest().authenticated();
})
```

**Depois:**
```java
.authorizeHttpRequests(req -> {
    req.requestMatchers("/actuator/**").permitAll();
    req.requestMatchers(HttpMethod.POST, "/login").permitAll();

    // Endpoints de cadastro: qualquer usuário autenticado
    req.requestMatchers("/api/v1/clientes/**").authenticated();
    req.requestMatchers("/api/v1/veiculos/**").authenticated();
    req.requestMatchers("/api/v1/funcionarios/**").authenticated();

    // Endpoints específicos por role
    req.requestMatchers("/api/v1/atendentes/**").hasRole("ATENDENTE");
    req.requestMatchers("/api/v1/gerentes/**").hasRole("GERENTE");
    req.requestMatchers("/api/v1/mecanicos/**").hasRole("MECANICO");

    req.anyRequest().authenticated();
})
```

## Endpoints Afetados

### ✅ Agora Acessíveis para Qualquer Usuário Autenticado:

1. **`POST /api/v1/clientes`** - Criar novo cliente
2. **`GET /api/v1/clientes`** - Listar clientes
3. **`POST /api/v1/veiculos`** - Criar novo veículo
4. **`GET /api/v1/veiculos/dono/{cpf}`** - Listar veículos por dono
5. **`POST /api/v1/funcionarios`** - Criar novo funcionário
6. **`GET /api/v1/funcionarios`** - Listar funcionários

### ✅ Endpoints com Restrição de Role (Mantidos):

- **`/api/v1/atendentes/**`** - Requer role `ATENDENTE`
- **`/api/v1/gerentes/**`** - Requer role `GERENTE`
- **`/api/v1/mecanicos/**`** - Requer role `MECANICO`

## Fluxo de Autorização

1. **Sem autenticação** → Retorna `401 Unauthorized`
2. **Com autenticação (qualquer role)** → Acesso permitido aos endpoints de cadastro
3. **Com autenticação (role específica)** → Acesso aos endpoints específicos daquela role

## Testes com Postman

Para testar os endpoints após as alterações, use a coleção Postman fornecida:

1. Execute o request **"0. Login (Obter JWT)"** para obter o token JWT
2. Execute o request **"1. Cadastrar Cliente"** para criar um novo cliente
3. Execute o request **"2. Cadastrar Veículo"** para criar um novo veículo
4. Execute o request **"3. Cadastrar Funcionário"** para criar um novo funcionário

Todos esses requests agora funcionarão com qualquer usuário autenticado (independente da role).

## Observações

- Os endpoints ainda requerem um JWT token válido no header `Authorization: Bearer {token}`
- O endpoint `/login` continua sendo público (sem autenticação necessária)
- Os endpoints de health check (`/actuator/**`) continuam públicos
- A ordem das regras de autorização é importante: as regras mais específicas vêm primeiro

## 🔍 Troubleshooting: Erro de Login ao Reiniciar a Aplicação

### ❓ Problema: Login retorna erro 401 após reiniciar a aplicação?

**Resposta: NÃO, os usuários de seed são persistidos no banco de dados!**

### 🔧 Como Funciona:

A aplicação possui um **DataInitializer** (`src/main/java/com/grupo51/oficinamecanica/cadastro/config/DataInitializer.java`) que:

1. **Executa apenas em ambiente DEV** (`@Profile("dev")`)
2. **Verifica se os usuários já existem** antes de criá-los:
   ```java
   if (usuarioRepository.findByLogin(login).isEmpty()) {
       usuarioRepository.save(new Usuario(...));
   }
   ```
3. **Salva os usuários NO BANCO DE DADOS** (PostgreSQL):
   - Login: `09151522037` (Mecânico) - Senha: `Senh@316497`
   - Login: `25390437021` (Atendente) - Senha: `Senh@316497`

### ✅ Configuração Atual

```yaml
# src/main/resources/application.yml
spring:
  profiles:
    active: dev
  
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/oficina_db}
    username: oficina_admin
    password: oficina_password
  
  jpa:
    hibernate:
      ddl-auto: update  # ← Cria/atualiza tabelas automaticamente
```

### 📊 Fluxo de Inicialização

```
1. Aplicação inicia
   ↓
2. Spring Boot cria o contexto
   ↓
3. DataInitializer é executado (profile=dev)
   ↓
4. Verifica se usuários existem no banco
   ├─ Se existem → Continua normalmente
   └─ Se não existem → Cria os usuários e salva no banco
   ↓
5. Banco de dados PostgreSQL persiste os dados
   ↓
6. Login funciona normalmente
```

### 🚀 Cenários Possíveis

| Cenário | O que acontece |
|---------|---|
| **Primeira execução** | DataInitializer cria os usuários no banco |
| **Reiniciar app (mesmo banco)** | ✅ Login funciona (usuários já existem no banco) |
| **Deletar banco manualmente** | ❌ Login falha até reiniciar (recria os usuários) |
| **Usar H2 em memória** | ❌ Dados perdidos ao reiniciar |
| **Mudar para profile=prod** | ❌ DataInitializer não executa |

### ⚠️ Se o Login Continuar Falhando:

1. **Verifique o banco de dados:**
   ```sql
   SELECT * FROM usuario;  -- Verificar se usuários existem
   ```

2. **Logs da aplicação:**
   - Procure por: `">>> Usuário de autenticação seed criado"`
   - Se não aparecer, significa que o DataInitializer não rodou

3. **Possíveis causas:**
   - Profile ativo NÃO é 'dev'
   - Banco de dados está desconectado
   - Permissões insuficientes no banco
   - Usuários já existem com valores diferentes

### 🔐 Credenciais de Teste Padrão

| Tipo | Login | Senha | Role |
|------|-------|-------|------|
| Mecânico | `09151522037` | `Senh@316497` | `MECANICO` |
| Atendente | `25390437021` | `Senh@316497` | `ATENDENTE` |

**Nota:** Essas credenciais devem ser alteradas em produção!

