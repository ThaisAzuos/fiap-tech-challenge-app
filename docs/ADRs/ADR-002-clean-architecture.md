# ADR 002 — Arquitetura Alvo: Clean Architecture/Hexagonal

**Status:** Aceita  
**Data:** 2026-03-14

---

## Resumo curto
Decidimos evoluir a arquitetura atual (organizada por features com camadas MVC) para **Clean Architecture/Hexagonal**, mantendo a organização por features mas separando claramente as responsabilidades em camadas independentes.

---

## Contexto
A aplicação atual está organizada em pacotes por domínio funcional (cadastro, atendimento, estoque, seguranca), cada um contendo controller, service, repository e model. Esta organização facilita o desenvolvimento mas mistura responsabilidades de diferentes camadas arquiteturais.

Para a Fase 2, precisamos implementar Clean Code e Clean Architecture para:
- Melhorar a testabilidade
- Facilitar manutenção e evolução
- Separar dependências externas
- Aplicar princípios SOLID

---

## Decisão
Adotar **Clean Architecture/Hexagonal** com a seguinte estrutura por feature:

```
src/main/java/com/grupo51/oficinamecanica/
├── {feature}/
│   ├── domain/
│   │   ├── model/          # Entidades, Value Objects
│   │   ├── service/        # Serviços de domínio
│   │   └── exception/      # Exceções de domínio
│   ├── application/
│   │   ├── usecase/        # Casos de uso (comandos e queries)
│   │   ├── dto/            # DTOs de entrada/saída
│   │   └── event/          # Eventos de domínio (se necessário)
│   └── infrastructure/
│       ├── controller/     # Controllers REST
│       ├── repository/     # Implementações JPA
│       └── config/         # Configurações específicas
├── common/
│   ├── exception/          # Exceções globais
│   ├── util/               # Utilitários
│   └── validation/         # Validações customizadas
└── config/                 # Configurações globais (Security, etc.)
```

### Regras de Dependência
- **Domain** não depende de ninguém
- **Application** depende apenas de **Domain**
- **Infrastructure** depende de **Application** e **Domain**
- **Common** pode ser usado por todas as camadas

### Migração Gradual
Como a aplicação já está funcional, faremos migração gradual:
1. Manter estrutura atual como referência
2. Criar nova estrutura lado a lado
3. Migrar features uma por uma
4. Remover código antigo após testes

---

## Consequências
- **Positivas:**
  - Melhor separação de responsabilidades
  - Maior testabilidade (especialmente domain e application)
  - Facilita mudanças de infraestrutura (banco, frameworks)
  - Código mais sustentável e legível

- **Negativas:**
  - Curva de aprendizado para a equipe
  - Trabalho inicial de refatoração
  - Possível duplicação temporária de código

---

## APIs Obrigatórias Identificadas

### Existentes (mantidas/ajustadas):
- `POST /api/v1/atendimento/os` - Abertura de OS
- `GET /api/v1/atendimento/os/{id}` - Consulta detalhes OS
- `PATCH /api/v1/atendimento/os/{id}/status` - Atualização de status

### A Implementar:
- `GET /api/v1/atendimento/os` - Listagem de OS
  - Parâmetros: page, size, sort (por status), filter (excluir finalizadas)
  - Retorno: Lista paginada com dados essenciais (id, status, dataAbertura, valorTotal, cliente, veiculo)
- `PATCH /api/v1/atendimento/os/{id}/aprovacao` - Aprovação de orçamento
  - Muda status de AGUARDANDO_APROVACAO para EM_EXECUCAO

### Fluxos Críticos:
1. **Abertura OS:** Cliente + Veículo → OS RECEBIDA
2. **Diagnóstico:** RECEBIDA → EM_DIAGNOSTICO → AGUARDANDO_APROVACAO
3. **Aprovação:** AGUARDANDO_APROVACAO → EM_EXECUCAO
4. **Execução:** EM_EXECUCAO → FINALIZADA → ENTREGUE
5. **Consulta:** Listagem filtrada + detalhes completos</content>
<parameter name="filePath">/home/celio-vetrano/FIAP - Software Architecture/fiap-tech-challenge-oficina/ADRs/ADR 002 — Arquitetura Alvo Clean Architecture.md
