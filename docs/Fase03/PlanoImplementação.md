Plano de Implementação do Tech Challenge

Contexto

O desafio tem prazo final de entrega em 15/05/2026 e envolve a elevação da aplicação para nível corporativo, com foco em segurança, escalabilidade, observabilidade e boas práticas de arquitetura.

Etapas e Prazos

1. Planejamento e Definições (Prazo: até 05/04/2026)

ADRs:
- Escolha da nuvem (AWS, Azure ou GCP).
- Padrão de comunicação entre serviços (REST vs gRPC).
- Estratégia de autenticação (JWT + CPF).

RFCs:
- Justificativa da escolha do banco de dados gerenciado.
- Estratégia de CI/CD.

Documentação inicial: cronograma, responsabilidades e divisão de tarefas.

2. Autenticação e API Gateway (Prazo: até 15/04/2026)

- Implementar API Gateway (ex.: AWS API Gateway ou Kong).
- Criar Function Serverless para:
- Validação de CPF.
- Consulta de status do cliente.
- Geração de token JWT.

ADR: Definição do fluxo de autenticação.

3. Estrutura de Repositórios e CI/CD (Prazo: até 25/04/2026)

- Criar 4 repositórios separados:
- Lambda (Function Serverless).
- Infraestrutura Kubernetes (Terraform).
- Infraestrutura Banco de Dados (Terraform).
- Aplicação principal.
- Configurar pipelines de CI/CD com deploy automático.

ADR: Estratégia de versionamento e proteção de branches.

4. Infraestrutura na Nuvem (Prazo: até 30/04/2026)

- Provisionar cluster Kubernetes com Terraform.
- Configurar banco de dados gerenciado.
- Implantar API Gateway e funções serverless.

ADR: Estratégia de escalabilidade (HPA, auto-scaling).

5. Monitoramento e Observabilidade (Prazo: até 05/05/2026)

- Integrar com Datadog ou New Relic.
- Configurar métricas:
    - Latência das APIs.
    - Consumo de CPU/memória.
    - Healthchecks e uptime.
    - Alertas de falhas.

- Criar dashboards:
    - Volume diário de ordens de serviço.
    - Tempo médio por status.
    - Erros e falhas.

ADR: Padrão de logs estruturados (JSON + correlação).

6. Documentação da Arquitetura (Prazo: até 10/05/2026)

- Diagrama de componentes (nuvem, APIs, banco, monitoramento).
- Diagrama de sequência (autenticação e abertura de ordens).
- RFCs e ADRs consolidados.
- Justificativa do banco de dados e modelo relacional.

7. Entregáveis Finais (Prazo: até 15/05/2026)

- Documento PDF único com:
    - Links dos 4 repositórios.
    - Link do vídeo de demonstração (até 15 min).
    - Links das documentações.
    - Confirmação do usuário soat-architecture nos repositórios.

ADRs Propostos

- Escolha da Nuvem: AWS pela maturidade em serviços serverless e integração com Datadog.
- Banco de Dados: PostgreSQL gerenciado pela flexibilidade e suporte a JSON.
- Autenticação: JWT com validação de CPF via Lambda.
- CI/CD: GitHub Actions com deploy automático em homologação e produção.
- Escalabilidade: Kubernetes com HPA baseado em métricas de CPU/memória.
- Observabilidade: Datadog para métricas, logs e alertas.

Conclusão

Este plano organiza as entregas em etapas progressivas, garantindo que até 15/05/2026 todos os requisitos obrigatórios sejam atendidos, com documentação completa e demonstração funcional.
