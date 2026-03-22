# Mini-guia JWT no Postman

Guia rápido para usar a collection `Oficina_Mecanica_API_Tech_Challenge.postman_collection.json` sem erros de autenticação (`401/403`) no fluxo da API.

Para padronização de nomes dos testes no Runner, consulte também: [`postman-estilo-testes.md`](./postman-estilo-testes.md).

## Pré-requisitos

- API rodando em `http://localhost:8080`
- Collection importada no Postman
- Variáveis de login preenchidas:
  - `login_cpf`
  - `login_senha`

## Modo manual (requisição isolada)

1. Execute `0. Login (Obter JWT)`.
2. Execute a requisição protegida desejada.
3. Se o token estiver ausente/expirado, a requisição é pulada e o Console orienta rodar login novamente.
4. Rode `0. Login (Obter JWT)` e reenvie a requisição pendente.

**Regra do modo manual:** faça login (`0. Login`) e depois execute as requisições protegidas normalmente. O pre-request injeta `Authorization` automaticamente com `jwt_token`.

**Importante:** não fixe `Authorization` manualmente nas requisições. A collection bloqueia header hardcoded e usa o token salvo em `jwt_token` após `0. Login (Obter JWT)`.

## Modo runner (Collection Runner)

1. Defina `runMode=runner` nas variáveis da collection.
2. (Opcional) Defina `always_refresh_jwt=true` para renovar JWT proativamente no runner.
3. Inicie a execução pelo runner.
4. A collection inicializa estado uma vez (`_runner_initialized`) e limpa variáveis transitórias.
5. Se o token expirar, o fluxo redireciona para `0. Login (Obter JWT)` e retoma automaticamente a requisição pendente.

## Variáveis de autenticação

- `jwt_token`: token JWT atual.
- `pending_request_name`: requisição aguardando autenticação.
- `auth_reason`: motivo do redirecionamento para login.
- `runMode`: define comportamento de execução (`runner` no Collection Runner).
- `_runner_initialized`: flag interna para reset único por execução.

## Fluxos da OS (novos passos)

Esta collection agora cobre a evolução completa da O.S. e um caminho opcional de cancelamento antes da aprovação.

### Fluxo feliz (sem cancelamento)

Use os passos exatamente nesta ordem:

1. `7. Abrir Ordem de Serviço`
2. `8. Avançar Status OS` (define `EM_DIAGNOSTICO`)
3. `8.1 Avançar OS para AGUARDANDO_APROVACAO`
4. `10. Incluir Peça na OS`
5. `11. Aprovar Orçamento da OS` (autoriza execução, vai para `EM_EXECUCAO`)
6. `11.1 Avançar OS para FINALIZADA`
7. `11.2 Avançar OS para ENTREGUE (devolução e pagamento)`
8. `9. Consultar Detalhes da OS` (opcional para conferência)

Variáveis da collection para este fluxo:

- `osId`: UUID da O.S. capturado no passo 7.
- `novoStatus`: controlado pelos passos de evolução (`8` e `8.1`).
- `ultimoStatusOS`: rastreia o status atual entre passos.
- `executarCancelamentoAntesAprovacao=false`: mantém o fluxo sem cancelamento.

Validação opcional de e-mails por fase (MailHog):

- `validarEmailFases=true`
- `mailhogBaseURL=http://localhost:8025`
- `mailhogEmailCountOS` é inicializado no passo 7 e incrementado nas fases.

### Fluxo com cancelamento (antes da aprovação)

Use os passos exatamente nesta ordem:

1. `7. Abrir Ordem de Serviço`
2. `8. Avançar Status OS` (vai para `EM_DIAGNOSTICO`)
3. `8.1 Avançar OS para AGUARDANDO_APROVACAO`
4. `10.1 Cancelar OS antes da aprovação (opcional)`

Configuração obrigatória para acionar o cancelamento:

- `executarCancelamentoAntesAprovacao=true`

Comportamento esperado:

- `10.1` envia `novoStatus=CANCELADA`.
- `ultimoStatusOS` passa para `CANCELADA`.
- passos `11`, `11.1` e `11.2` são ignorados automaticamente quando a O.S. já está cancelada.

Validação opcional de e-mail de cancelamento:

- mantenha `validarEmailFases=true` para verificar incremento de e-mails no MailHog também no cenário de cancelamento.

### Blocos rápidos (copiar e colar no Runner)

Use os pares `chave=valor` abaixo como preset de variáveis da collection.

**Fluxo feliz (recomendado para execução padrão):**

```bash
runMode=runner
always_refresh_jwt=true
executarCancelamentoAntesAprovacao=false
validarEmailFases=false
mailhogBaseURL=http://localhost:8025
```

**Fluxo com cancelamento (antes da aprovação):**

```bash
runMode=runner
always_refresh_jwt=true
executarCancelamentoAntesAprovacao=true
validarEmailFases=false
mailhogBaseURL=http://localhost:8025
```

Se quiser validar e-mails por fase em qualquer fluxo, altere apenas:

```bash
validarEmailFases=true
```

### Tabela rápida de variáveis (consulta visual)

| Variável | Fluxo feliz | Fluxo com cancelamento |
|---|---|---|
| `runMode` | `runner` | `runner` |
| `always_refresh_jwt` | `true` | `true` |
| `executarCancelamentoAntesAprovacao` | `false` | `true` |
| `validarEmailFases` | `false` (ou `true`, se quiser validar e-mails por fase) | `false` (ou `true`, se quiser validar e-mails por fase) |
| `mailhogBaseURL` | `http://localhost:8025` | `http://localhost:8025` |
| `osId` | preenchido automaticamente no passo 7 | preenchido automaticamente no passo 7 |
| `ultimoStatusOS` | preenchido automaticamente a cada transição de status | preenchido automaticamente a cada transição de status |
| `mailhogEmailCountOS` | inicializado/preenchido automaticamente durante o fluxo | inicializado/preenchido automaticamente durante o fluxo |

## Troubleshooting rápido

- `401 Unauthorized`: token ausente/expirado/inválido. Rode `0. Login (Obter JWT)`.
- `403 Forbidden`: token não aceito no filtro JWT ou perfil sem acesso ao endpoint.
- `JSONError: No data, empty input`: resposta sem corpo; os scripts da collection já fazem parse defensivo.

