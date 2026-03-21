# SonarQube em producao (guia pratico)

Este guia adiciona um ambiente SonarQube separado do `docker-compose.yml` principal para evitar impacto no fluxo de desenvolvimento da API.

## Arquivos criados

- `docker-compose.sonar.yml`
- `.env.sonar.example`
- `.github/workflow-templates/sonar-maven-template.yml`

## 1) Subir SonarQube com Postgres

```bash
cd "/home/celio-vetrano/FIAP - Software Architecture/fiap-tech-challenge-oficina"
cp .env.sonar.example .env.sonar
docker compose --env-file .env.sonar -f docker-compose.sonar.yml up -d
```

Acesso web: `http://localhost:9000`

Usuario inicial: `admin`
Senha inicial: `admin` (sera solicitado trocar no primeiro login)

## 2) Criar token e projeto

1. Em SonarQube, crie um projeto com a chave `com.grupo51:oficinamecanica`.
2. Gere um token de analise em **My Account > Security**.
3. Guarde o token para usar no pipeline.

## 3) Rodar analise local Maven

```bash
cd "/home/celio-vetrano/FIAP - Software Architecture/fiap-tech-challenge-oficina"
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<SEU_TOKEN>
```

## 4) Qualidade minima recomendada (Quality Gate)

- Coverage: `>= 70%`
- Duplicated lines: `<= 3%`
- New code smells: `0 blocker`
- New vulnerabilities: `0`

## 5) Pipeline CI/CD

O workflow esta ativo em `.github/workflows/sonar.yml`.

O template de referencia esta em `.github/workflow-templates/sonar-maven-template.yml`.

Secrets obrigatorios no repositorio GitHub:

| Secret           | Valor esperado                                  |
|------------------|-------------------------------------------------|
| `SONAR_HOST_URL` | URL do servidor (ex: `http://seu-servidor:9000`) |
| `SONAR_TOKEN`    | Token gerado em My Account > Security            |

Gatilhos configurados:

- `push` para `main` e `develop`
- `pull_request` para `main` e `develop`
- `workflow_dispatch` (execucao manual via GitHub Actions)

O workflow executa:

1. Checkout com historico completo (`fetch-depth: 0`).
2. Build e testes com cobertura JaCoCo (`mvnw clean verify`).
3. Analise SonarQube aguardando resultado do Quality Gate.
4. Upload do relatorio JaCoCo como artifact (retido por 7 dias).

## 6) Operacao e manutencao

Ver logs:

```bash
docker compose --env-file .env.sonar -f docker-compose.sonar.yml logs -f sonarqube
```

Parar ambiente:

```bash
docker compose --env-file .env.sonar -f docker-compose.sonar.yml down
```

Parar e remover volumes (reset completo):

```bash
docker compose --env-file .env.sonar -f docker-compose.sonar.yml down -v
```

## Notas

- Este setup simula um ambiente de produção para validação local/servidor dedicado.
- Para producao real, use TLS reverso (Nginx/Traefik), backup de volumes e monitoramento.

