# Oficina Clean MVP

Modulo MVP da Fase 2 para iniciar Clean Architecture em pacote separado, sem impactar o build atual da API principal.

## Indice

- [Status do modulo](#status-do-modulo)
- [Estrutura principal](#estrutura-principal)
- [Como executar testes](#como-executar-testes)
- [Como executar runner local](#como-executar-runner-local)
- [Troubleshooting rapido](#troubleshooting-rapido)
- [Relacao com o projeto principal](#relacao-com-o-projeto-principal)

## Status do modulo

- Estrutura base de Clean Architecture criada.
- Runner local disponivel para validacao rapida do fluxo.
- Evolucao incremental prevista para integrar novos casos de uso.

## Estrutura principal

- `domain`: regras de negocio e entidades puras
- `application`: casos de uso e portas (interfaces)
- `infrastructure`: adaptadores, implementacoes concretas e runner

## Como executar testes

```bash
cd "modules/oficina-clean-mvp"
mvn test
```

## Como executar runner local

```bash
cd "modules/oficina-clean-mvp"
mvn -q -DskipTests exec:java -Dexec.mainClass=com.grupo51.oficina.cleanmvp.infrastructure.runner.CleanMvpRunner
```

Se o `exec-maven-plugin` nao estiver disponivel no cache local:

```bash
cd "modules/oficina-clean-mvp"
mvn -q -DskipTests package
java -cp target/classes com.grupo51.oficina.cleanmvp.infrastructure.runner.CleanMvpRunner
```

## Troubleshooting rapido

Erro de dependencia/plugin Maven:

```bash
cd "modules/oficina-clean-mvp"
mvn -e -DskipTests=false test
```

Classe runner nao encontrada no fallback com `java -cp`:

```bash
cd "modules/oficina-clean-mvp"
mvn -q -DskipTests package
find target/classes -type f | grep CleanMvpRunner
```

## Relacao com o projeto principal

- README raiz: `README.md`
- Documentacao geral da evolucao: `ADRs/ADR — Evolução da Aplicação.md`

