# ADR 001 — Autenticação e Autorização com JWT

**Status:** Aceita  
**Data:** 2026-02-19

---

## Resumo curto
Decidimos implementar autenticação baseada em **JWT** e registro de usuários com **senha criptografada (BCrypt)**, usando **CPF** como login e aplicando uma política de senha (8–16 caracteres, ao menos uma maiúscula, uma minúscula, um número e um caractere especial).

> “Eu entendo a tarefa. Implementarei um sistema abrangente de autenticação e autorização.”

---

## Contexto
O sistema precisa de um mecanismo seguro e padronizado de autenticação e autorização para proteger endpoints REST e gerenciar usuários do domínio (funcionários/mecânicos). Requisitos principais:

- **Login por CPF** e armazenamento de senha segura.
- **Política de senha**: 8–16 caracteres; pelo menos 1 maiúscula, 1 minúscula, 1 número e 1 caractere especial.
- **Endpoint `/login`** que emite **JWT** após autenticação bem-sucedida.
- **Validação de JWT** em todas as requisições a endpoints protegidos.
- **BCrypt** para hashing de senhas.
- **Spring Security** configurado em modo **stateless**.

---

## Decisão
Implementar a solução composta por:

1. **Registro de usuário**
    - Adicionar campo `senha` à entidade `Funcionario` e usar `cpf` como `login`.
    - Criar `FuncionarioCadastroDTO` com validações (`@Size`, `@Pattern`) para impor a política de senha.
    - Criptografar senhas com `BCryptPasswordEncoder` antes de persistir.

2. **Autenticação e tokens**
    - Criar `JwtTokenService` para geração e validação de JWTs; segredo configurável via `api.security.token.secret`.
    - Implementar `LoginController` com `POST /login` que autentica via `AuthenticationManager` e retorna token JWT.

3. **Validação por requisição**
    - Implementar `JwtTokenFilter` (estendendo `OncePerRequestFilter`) que extrai o token do cabeçalho `Authorization`, valida com `JwtTokenService` e popula o `SecurityContext`.
    - Atualizar `SecurityConfig` para:
        - Desabilitar CSRF.
        - Definir sessão como `STATELESS`.
        - Permitir `POST /login` sem autenticação.
        - Exigir autenticação para demais endpoints.
        - Inserir `JwtTokenFilter` antes de `UsernamePasswordAuthenticationFilter`.
        - Expor beans `AuthenticationManager` e `PasswordEncoder`.

4. **Repositório e modelos**
    - Corrigir `UsuarioRepository` para estender `JpaRepository<Usuario, UUID>` e expor `findByLogin(String login)`.
    - Garantir que `Usuario` e `Funcionario` tenham construtores e campos necessários (login, senha, perfil).

---

## Consequências

**Benefícios**
- Autenticação sem estado, escalável e adequada para APIs REST.
- Senhas armazenadas de forma segura (BCrypt).
- Validação centralizada de tokens, reduzindo duplicação de lógica.
- Política de senha aplicada no DTO, garantindo consistência.

**Riscos / Pontos de atenção**
- **Segredo do JWT** deve ser protegido (variáveis de ambiente ou secret manager); não codificar no código-fonte.
- Dependência JWT (`com.auth0:java-jwt` ou `io.jsonwebtoken:jjwt`) deve estar presente no `pom.xml`.
- Verificar existência e contratos de objetos de valor e enums (por exemplo, `Cpf`, `Email`, `Cargo`, `Perfil`) usados nas suposições.
- Implementar tratamento de exceções padronizado para tokens inválidos/expirados e falhas de autenticação.

---

## Implementação — arquivos criados / modificados (resumo)
**Pacote `seguranca`**
- `LoginController.java` — endpoint `POST /login`; `LoginRequest` DTO.
- `service/JwtTokenService.java` — geração/validação de JWT; usa `@Value("${api.security.token.secret}")`.
- `JwtTokenFilter.java` — filtro que valida token e popula `SecurityContextHolder`.
- `SecurityConfig.java` — configuração do `SecurityFilterChain`, sessão `STATELESS`, registro do filtro.
- `repository/UsuarioRepository.java` — interface `JpaRepository<Usuario, UUID>` com `findByLogin`.

**Pacote `cadastro`**
- `Funcionario.java` — adicionado campo `senha`; construtor atualizado; campos opcionais para especialidade/registroFuncional.
- `FuncionarioCadastroDTO.java` — DTO com `nome`, `cpf`, `senha` e validações de senha.
- `FuncionarioService.java` — criptografia de senha com `PasswordEncoder`, criação de `Usuario`.
- `FuncionarioController.java` — recebe `@Valid FuncionarioCadastroDTO`, retorna `201 Created`.

---

## Trechos de código essenciais (exemplos prontos para colar)

### `JwtTokenService.java` (essencial)
```java
@Service
public class JwtTokenService {
  @Value("${api.security.token.secret}")
  private String secret;
  private final long expiration = 3600_000; // 1h

  public String gerarToken(String login) {
    return JWT.create()
      .withSubject(login)
      .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
      .sign(Algorithm.HMAC256(secret));
  }

  public String getSubject(String token) {
    return JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getSubject();
  }
}

```

