# Validacao Swagger - Estado Atual

Data de revisao: 2026-03-21

Este documento descreve o estado esperado da documentacao Swagger/OpenAPI apos os ajustes de fluxo e exemplos.

## Escopo atualizado

- Fluxo numerado no Swagger alinhado ao Postman (0 a 14).
- Exemplos de request editaveis no `Try it out`.
- Enums corrigidos para refletir o dominio real.
- Operacoes de `Atendimento` e `Agendamento` com verbos/paths corretos.

## Endpoints documentados (17)

### Autenticacao

- `POST /login`

### Clientes

- `POST /api/v1/clientes`
- `GET /api/v1/clientes`

### Veiculos

- `POST /api/v1/veiculos`
- `GET /api/v1/veiculos/dono/{cpf}`

### Funcionarios

- `POST /api/v1/funcionarios`
- `GET /api/v1/funcionarios`

### Pecas

- `POST /api/v1/pecas`
- `GET /api/v1/pecas`

### Agendamentos

- `POST /api/v1/agendamentos`
- `GET /api/v1/agendamentos/{id}`

### Atendimento (Ordem de Servico)

- `POST /api/v1/atendimento/os`
- `PATCH /api/v1/atendimento/os/{osId}/status`
- `GET /api/v1/atendimento/os/{osId}`
- `POST /api/v1/atendimento/os/{osId}/pecas`
- `PATCH /api/v1/atendimento/os/{osId}/aprovacao`
- `GET /api/v1/atendimento/os`

## Fluxo operacional recomendado

0. Login
1. Cadastrar cliente
2. Cadastrar veiculo
3. Cadastrar mecanico
4. Cadastrar funcionario
5. Realizar agendamento
6. Cadastrar peca
7. Abrir O.S.
8. Avancar status da O.S.
9. Consultar detalhes da O.S.
10. Incluir peca na O.S.
11. Aprovar orcamento
12. Listar O.S.
13. Listar clientes
14. Listar veiculos por dono

## Pontos de consistencia de dominio

### TipoAgendamento

- `ANALISE`
- `EXECUCAO`

### StatusOS

- `RECEBIDA`
- `EM_DIAGNOSTICO`
- `AGUARDANDO_APROVACAO`
- `EM_EXECUCAO`
- `FINALIZADA`
- `ENTREGUE`
- `CANCELADA`

## Check rapido de conferencia manual

Com a aplicacao em execucao, valide URLs do Swagger:

```bash
curl -s -I http://localhost:8080/swagger-ui/index.html
curl -s http://localhost:8080/v3/api-docs | jq '.paths | keys | length'
curl -s http://localhost:8080/v3/api-docs | jq '.components.securitySchemes'
```

Resultados esperados:

- Swagger UI respondendo `200`.
- Documento OpenAPI contendo os paths acima.
- Security scheme `Bearer Authentication` presente.

## Validacao tecnica executada neste ajuste

Foi executada compilacao local para garantir integridade apos as mudancas de anotacoes e exemplos:

```bash
./mvnw -q -DskipTests compile
```

Sem erros de compilacao.

