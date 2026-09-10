# ADR 009 — Refatoração do OS Service: Publisher, Consumer e Orquestrador da Saga

**Status:** Aceita
**Data:** 2026-07-11
**Fase:** Tech Challenge Fase 4 — FIAP SOAT

---

## Contexto

O Dia 3 do plano de execução previa refatorar o `fiap-tech-challenge-app` (agora OS Service) para: publicar o evento `OrdemServicoCriada` na abertura de uma OS; consumir os eventos `OrcamentoAprovado`/`OrcamentoReprovado` (Billing Service), `PagamentoConfirmado`/`PagamentoFalhou` (Billing Service) e `ExecucaoConcluida` (Execution Service); e implementar o orquestrador da Saga dentro do próprio OS Service, incluindo a lógica de compensação (cancelamento da OS).

Diferente dos Dias 1 e 2 (documentação e infraestrutura), este dia altera código de produção de um sistema já existente, com testes automatizados (`@SpringBootTest` e testes unitários) que precisam continuar passando sem que fosse possível rodar `mvn test` a partir do ambiente onde esta refatoração foi preparada (sem acesso de rede ao repositório de dependências Maven a partir da ponte de arquivos). Por isso, todas as decisões abaixo priorizam **não invadir/reescrever** a lógica de domínio já testada.

## Decisões

### 1. Estratégia aditiva ("strangler fig"), sem alterar `OrdemServico`/`StatusOS`

O `SagaOrchestratorService` novo **reutiliza** os métodos públicos já existentes e testados de `AtendimentoService` (`aprovarOrcamento`, `cancelarOrdemServico`, `concluirOrdemServico`), que por sua vez usam `OrdemServico.atualizarStatus()`/`cancelar()` já testados. Nenhuma classe de domínio (`OrdemServico`, `StatusOS`, `ItemOS`, `ItemServicoOS`) foi alterada. Isso reduz o risco de regressão a zero nesses arquivos, ao custo de um mapeamento simplificado entre eventos e status (item 2).

### 2. Mapeamento evento → status da OS

O enum `StatusOS` existente não tem um estado dedicado para "orçamento aprovado, aguardando pagamento". Optou-se por:

| Evento recebido | Ação no OS Service |
|---|---|
| `OrcamentoAprovado` | `aprovarOrcamento(osId)` → `AGUARDANDO_APROVACAO` → `EM_EXECUCAO` |
| `PagamentoConfirmado` | Somente log/auditoria — sem transição adicional de status |
| `ExecucaoConcluida` | `concluirOrdemServico(osId)` → `EM_EXECUCAO` → `FINALIZADA` |
| `OrcamentoReprovado` | Compensação: `cancelarOrdemServico(osId, motivo)` → `CANCELADA` |
| `PagamentoFalhou` | Compensação: `cancelarOrdemServico(osId, motivo)` → `CANCELADA` |

Essa simplificação é suficiente para o escopo didático da Fase 4 e é revisável em uma fase futura (ex.: adicionar um novo status `AGUARDANDO_PAGAMENTO`), o que exigiria alterar `StatusOS`, `OrdemServico.atualizarStatus()` e seus testes — deliberadamente fora do escopo desta refatoração.

### 3. `sagaId == ordemServicoId`

Mantido da ADR-007 (`docs/arquitetura/fase4-visao-geral.md`): não há campo/entidade nova de Saga — o `ordemServicoId` funciona como identificador da Saga em todo o fluxo, publicado e consumido em todos os eventos.

### 4. Correção do contrato `OrdemServicoCriada` (clienteId/veiculoId → clienteCpf/veiculoPlaca)

Ao implementar o publisher, constatou-se que o domínio `Cliente` não possui um `id` (UUID) próprio — é identificado por CPF — e `Veiculo` é identificado pela placa (chave natural), sem UUID próprio. O contrato desenhado no Dia 1 (`docs/arquitetura/eventos/ordem-servico-criada.schema.json`) assumia `clienteId`/`veiculoId` como UUID. O schema foi corrigido neste dia para `clienteCpf`/`veiculoPlaca` (string), e o `status` do payload passou de um valor fixo fictício (`AGUARDANDO_ORCAMENTO`, que não existe em `StatusOS`) para o status real da OS no momento da publicação (`RECEBIDA`, via `os.getStatus().name()`).

### 5. Serialização manual em JSON (não usa o cabeçalho `__TypeId__` do Jackson)

Mantida a decisão de arquitetura registrada no planejamento: como Billing Service e Execution Service são bases de código Java separadas, o mecanismo padrão do `Jackson2JsonMessageConverter` do Spring AMQP (que grava o nome completo da classe Java do produtor num cabeçalho e exige a mesma classe no consumidor) não funciona entre serviços distintos. Em vez disso: o publisher serializa manualmente (`ObjectMapper.writeValueAsString`) e envia como `String`/JSON puro; o listener lê o `Message` bruto, decodifica UTF-8 e faz o parse com `ObjectMapper` para um envelope genérico (`EventoSagaRecebido`, payload como `JsonNode`), despachando pela routing key.

### 6. Beans de mensageria protegidos por `saga.messaging.enabled` (padrão `true`, `false` nos testes)

O Spring Boot autoconfigura um `RabbitAdmin` que, ao subir o contexto (`ContextRefreshedEvent`), tenta **declarar** todo bean `Exchange`/`Queue`/`Binding` no broker — ou seja, mesmo sem nenhum `@RabbitListener`, apenas registrar esses beans já dispara uma tentativa de conexão real ao RabbitMQ. Já um bean `@RabbitListener` (`SimpleMessageListenerContainer`) é ativo por si só e tenta se conectar e consumir assim que o contexto sobe. Isso quebraria os 8 arquivos `@SpringBootTest` existentes, que não têm um broker RabbitMQ disponível no ambiente de teste.

Solução: `RabbitMQConfig` (declara exchange/filas/bindings) e `SagaEventListener` (o `@RabbitListener`) são anotados com `@ConditionalOnProperty(prefix = "saga.messaging", name = "enabled", havingValue = "true", matchIfMissing = true)`. O valor padrão é `true` (ambiente real), e `src/test/resources/application.yml` define `saga.messaging.enabled: false`, excluindo essas duas classes do contexto de teste.

Já o `RabbitTemplate`/`ConnectionFactory` (autoconfigurados pelo Spring Boot assim que `spring-boot-starter-amqp` está no classpath) **não** precisaram do mesmo tratamento: a criação desses beans é preguiçosa — só tentam conectar no primeiro uso real (publicação de uma mensagem), não na subida do contexto. Por segurança, o `OrdemServicoEventPublisher` também checa a flag `saga.messaging.enabled` antes de publicar qualquer mensagem.

### 7. Compatibilidade retroativa do construtor de `AtendimentoService`

`AtendimentoServiceTest.java` instancia `AtendimentoService` diretamente com o construtor de 5 argumentos existente (sem o novo `OrdemServicoEventPublisher`). Para não quebrar esse teste: o construtor de 5 argumentos foi mantido e agora delega para um novo construtor de 6 argumentos (marcado `@Autowired`, usado pelo Spring) passando `eventPublisher = null`. O método `abrirOrdem()` só chama `eventPublisher.publicarOrdemServicoCriada(...)` quando `eventPublisher != null` — mesmo padrão já usado nesta classe para `Optional<EmailService>`.

## Alternativas consideradas

- **Adicionar um novo status `AGUARDANDO_PAGAMENTO` ao `StatusOS`**: rejeitado nesta fase. Embora `@Enumerated(EnumType.STRING)` tornasse isso seguro no banco, a lógica de validação de transição em `OrdemServico.atualizarStatus()` (baseada em `ordinal()`) e o teste unitário dessa classe teriam que ser revisados sem a possibilidade de rodar a suíte de testes neste ambiente — risco desnecessário frente ao prazo. Fica registrado como evolução futura.
- **Usar `Jackson2JsonMessageConverter` com `__TypeId__`**: rejeitado — acopla o contrato de mensagem à classe Java do produtor, inviável entre bases de código diferentes (Billing/Execution Service ainda serão criados do zero).
- **Sagas separadas por entidade própria no banco**: rejeitado por simplicidade (ver ADR-007), mantendo `sagaId == ordemServicoId`.

## Consequências

- Nenhuma classe de domínio ou teste existente foi alterada; apenas `AtendimentoService` ganhou um construtor novo (aditivo) e uma chamada guardada por null-check.
- `docs/arquitetura/eventos/ordem-servico-criada.schema.json` foi atualizado para refletir `clienteCpf`/`veiculoPlaca`/`status` reais — Billing Service e Execution Service (a serem implementados nos Dias 4 e 5) devem seguir o schema corrigido, não a versão original do Dia 1.
- Não foi possível compilar/rodar `mvn test` neste ambiente (sem acesso de rede a partir da ponte com o computador do usuário) — a verificação de build deve ser feita localmente antes do push.
- Ambiente real (`saga.messaging.enabled=true` por padrão): ao instalar o RabbitMQ (Dia 2, `docker-compose.fase4.yml` ou o módulo Terraform `messaging`), o OS Service passa a publicar e consumir os eventos automaticamente, sem nenhuma alteração de código.
