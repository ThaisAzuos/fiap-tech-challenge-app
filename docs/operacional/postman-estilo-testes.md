# Mini-guia de estilo para testes da collection (Postman)

Objetivo: manter os nomes de `pm.test(...)` consistentes no Runner e facilitar o filtro visual por passo.

## Regras (curtas e obrigatórias)

1. **Prefixo por passo**
   - Sempre iniciar o nome do teste com `[PASSO X]`.
   - Exemplo: `[PASSO 7] Status HTTP 201 - O.S. criada com sucesso`.

2. **Vocabulário padronizado**
   - Usar `Status HTTP` (evitar `Status code`).
   - Usar termos em português com acentuação consistente (`contém`, `não`, `método`) quando aplicável.

3. **Descrição curta e orientada ao resultado**
   - Formato recomendado: `[PASSO X] <critério> - <resultado esperado>`.
   - Evitar nomes longos ou ambíguos; manter foco no comportamento validado.

## Regra 4 (opcional)

4. **Limite de tamanho do nome do teste**
   - Preferir nomes com até **90 caracteres** para manter boa leitura no Runner.
   - Para mensagens dinâmicas longas (erros), manter o `pm.test` curto e detalhar contexto em `console.log(...)`.

## Modelo rápido

```javascript
pm.test('[PASSO 8] Status HTTP 204 - status da O.S. atualizado', function () {
  pm.response.to.have.status(204);
});
```

## Nota

Quando houver falha dinâmica (mensagem montada em runtime), mantenha o mesmo prefixo `[PASSO X]` no início da string para o Runner agrupar visualmente os erros.

| Cenário | Bom | Ruim |
|---|---|---|
| Revisão em PR | `[PASSO 8] Status HTTP 204 - status da O.S. atualizado` | `Status code OK` |
| Falha dinâmica (richFailureMsg) | `const richFailureMsg = '[PASSO 7] Resposta sem id | statusHTTP=500 | método=POST | url=/api/v1/atendimento/os | trechoBody=...'` | `const richFailureMsg = 'Erro genérico'` |



