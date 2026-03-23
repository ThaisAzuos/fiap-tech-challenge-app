# Swagger - Exemplos Reais e Editaveis

Este guia foi atualizado para refletir o fluxo real no Swagger, com a mesma ordem da colecao Postman.

## Objetivo

- Deixar os payloads prontos para executar no `Try it out`.
- Permitir alterar qualquer campo antes do `Execute`.
- Manter o fluxo operacional correto (0 a 14).

## Dados seed uteis

- Cliente seed: `73383053036` (Joao da Silva)
- Veiculo seed: `ABC1D23` (dono `73383053036`)
- Login mecanico: `09151522037` / `Senh@316497`
- Login atendente: `25390437021` / `Senh@316497`

## Fluxo oficial (igual Postman)

0. `POST /login`
1. `POST /api/v1/clientes`
2. `POST /api/v1/veiculos`
3. `POST /api/v1/funcionarios` (mecanico)
4. `POST /api/v1/funcionarios` (outro funcionario)
5. `POST /api/v1/agendamentos`
6. `POST /api/v1/pecas`
7. `POST /api/v1/atendimento/os`
8. `PATCH /api/v1/atendimento/os/{osId}/status?novoStatus=...`
9. `GET /api/v1/atendimento/os/{osId}`
10. `POST /api/v1/atendimento/os/{osId}/pecas`
11. `PATCH /api/v1/atendimento/os/{osId}/aprovacao`
12. `GET /api/v1/atendimento/os?page=0&size=10&sort=dataAbertura,desc`
13. `GET /api/v1/clientes`
14. `GET /api/v1/veiculos/dono/{cpf}`

## Enums corretos do dominio

### Tipo de agendamento

- `ANALISE`
- `EXECUCAO`

### Status de O.S.

- `RECEBIDA`
- `EM_DIAGNOSTICO`
- `AGUARDANDO_APROVACAO`
- `EM_EXECUCAO`
- `FINALIZADA`
- `ENTREGUE`
- `CANCELADA`

Fluxo normal recomendado:

`RECEBIDA -> EM_DIAGNOSTICO -> AGUARDANDO_APROVACAO -> EM_EXECUCAO -> FINALIZADA -> ENTREGUE`

## Exemplos base para copiar/editar

### 0) Login

```json
{
  "login": "09151522037",
  "senha": "Senh@316497"
}
```

### 1) Cliente

```json
{
  "nome": "Marina Oliveira",
  "cpf": "52998224725",
  "email": "marina.oliveira@email.com.br",
  "telefone": "11987654321",
  "endereco": {
    "logradouro": "Avenida Paulista",
    "numero": "1000",
    "complemento": "Apto 201",
    "bairro": "Bela Vista",
    "cidade": "Sao Paulo",
    "uf": "SP",
    "cep": "01310100"
  }
}
```

### 2) Veiculo

```json
{
  "placa": "DEF4G56",
  "modelo": "Corolla",
  "marca": "Toyota",
  "ano": 2023,
  "cor": "Preto",
  "cpfDono": "73383053036"
}
```

### 5) Agendamento

```json
{
  "clienteId": "73383053036",
  "veiculoId": "ABC1D23",
  "recursoId": "09151522037",
  "dataHoraInicio": "2026-06-10T09:00:00",
  "dataHoraFim": "2026-06-10T11:00:00",
  "tipo": "ANALISE"
}
```

### 6) Peca

```json
{
  "nome": "Filtro de Oleo Mann W712",
  "preco": 48.90,
  "quantidadeEstoque": 100
}
```

### 7) Abrir O.S.

```json
{
  "placa": "ABC1D23",
  "descricaoProblema": "Barulho na transmissao ao acelerar. Falha intermitente."
}
```

### 10) Incluir peca na O.S.

```json
{
  "pecaId": "550e8400-e29b-41d4-a716-446655440001",
  "quantidade": 1
}
```

## Como usar no Swagger

1. Abra `http://localhost:8080/swagger-ui/index.html`.
2. Execute o endpoint `0. Login` e copie `token`.
3. Clique em `Authorize` e informe `Bearer <token>`.
4. Siga os endpoints pela numeracao do summary (0 ate 14).
5. Em cada endpoint, use o exemplo preenchido, ajuste campos e execute.

## Observacao importante

Os exemplos de data/hora em agendamento devem estar no presente/futuro por causa das validacoes (`@FutureOrPresent` e `@Future`).

- Use datas futuras para agendamento.
- CPF deve ter 11 digitos sem pontuacao.
- Placa deve seguir formato aceito pelo sistema.
- Senha de funcionario deve seguir a regra de complexidade.

