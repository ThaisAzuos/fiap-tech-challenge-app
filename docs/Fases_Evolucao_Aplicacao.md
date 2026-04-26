# Fases de Evolução da Aplicação da Oficina Mecânica

Este documento descreve as fases de evolução da aplicação da oficina mecânica, baseada no feedback e comentários sobre o estado atual do projeto (Fase 2). O objetivo é estruturar melhorias funcionais, arquiteturais e documentais para alcançar uma solução mais completa e aderente aos requisitos do Tech Challenge.

## Fase Atual (Fase 2) - Estado Atual da Aplicação

### Descrição
A aplicação já apresenta uma base sólida com organização técnica e cobertura de fluxos importantes do Tech Challenge. Inclui Docker, Docker Compose, Swagger, testes automatizados, manifests de Kubernetes (deployment, service, configmap, secret, HPA), Terraform para provisionamento de EKS e RDS, pipeline CI/CD, documentação cuidada com guias operacionais e desenhos de arquitetura, segurança básica com autenticação JWT, notificações por e-mail e qualidade de código via SonarQube.

### Pontos Positivos Identificados
- **Cobertura Funcional**: Consulta de status da OS, atualização de status com envio de e-mail ao cliente.
- **Infraestrutura**: Manifests de Kubernetes completos, Terraform provisionando cluster EKS e banco RDS.
- **Documentação**: Guias claros para execução local (Docker, Kubernetes, testes, Postman, SonarQube), desenhos de arquitetura bem produzidos.
- **Estrutura Geral**: Projeto organizado, esforço claro para entregar solução completa, vídeo de apresentação dentro do tempo e claro.

### Tecnologias Envolvidas
- Java 21, Spring Boot
- PostgreSQL, Docker, Kubernetes, Terraform
- JWT, MailHog, SonarQube
- Maven, Postman

### Cronograma
- Já implementada (Fase 2 concluída)

### Responsáveis
- Equipe de desenvolvimento (desenvolvedores, DevOps)

## Fase 3 - Melhorias Funcionais

### Descrição
Focar na aderência completa da API ao enunciado do Tech Challenge, implementando fluxos funcionais ausentes ou parciais.

### Melhorias Específicas
1. **Abertura Completa da OS**: Modificar a abertura da Ordem de Serviço para receber todos os dados completos do cliente, veículo, serviços e peças no próprio fluxo, não apenas placa e descrição do problema.
2. **Fluxo de Aprovação de Orçamento**: Implementar um fluxo externo direto para aprovação ou recusa do orçamento pelo cliente, além da aprovação autenticada existente.
3. **Ordenação de Ordens**: Implementar ordenação específica das ordens por prioridade de status e antiguidade, não dependente apenas da paginação padrão.

### Tecnologias Envolvidas
- Java/Spring Boot (APIs REST)
- JPA/Hibernate (persistência)
- JWT (autenticação)
- Email (notificações)

### Cronograma Estimado
- 2-3 semanas
- Semana 1: Análise e design dos novos endpoints
- Semana 2: Implementação da abertura completa da OS
- Semana 3: Fluxo de aprovação e ordenação, testes

### Responsáveis
- Desenvolvedor Backend (para APIs e lógica de negócio)
- QA (para testes funcionais)

## Fase 3 - Melhorias Arquiteturais

### Descrição
Aprofundar a aplicação de Clean Architecture/Hexagonal em todos os módulos, melhorar isolamento do domínio e corrigir questões de infraestrutura e segurança.

### Melhorias Específicas
1. **Clean Architecture Completa**: Aplicar arquitetura hexagonal em todos os módulos (não apenas atendimento), com separação clara de ports e adapters.
2. **Isolamento do Domínio**: Remover anotações JPA das entidades do domínio, evitando dependência direta de persistência.
3. **HPA Aprimorado**: Configurar Horizontal Pod Autoscaler para monitorar CPU e memória, conforme enunciado.
4. **Gestão de Secrets**: Melhorar prática de segurança, não expondo valores de secrets em texto no repositório (usar variáveis de ambiente ou ferramentas como Vault).
5. **Correção da Pipeline**: Garantir que a pipeline aplique o banco de dados no cluster Kubernetes e corrija inconsistências no rollout (nomes de deployments).

### Tecnologias Envolvidas
- Java/Spring Boot (refatoração para Clean Architecture)
- Kubernetes (HPA, secrets)
- Terraform (infraestrutura)
- CI/CD Pipeline (GitHub Actions/Jenkins)

### Cronograma Estimado
- 3-4 semanas
- Semana 1: Refatoração para Clean Architecture completa
- Semana 2: Isolamento do domínio e ajustes em entidades
- Semana 3: Configuração HPA e gestão de secrets
- Semana 4: Correção da pipeline e testes

### Responsáveis
- Arquiteto de Software (para design arquitetural)
- DevOps Engineer (para Kubernetes, pipeline, Terraform)

## Fase 3 - Melhorias na Documentação

### Descrição
Aprimorar a documentação para facilitar ainda mais a leitura e entendimento da solução.

### Melhorias Específicas
1. **README Aprimorado**: Destacar mais informações principais, detalhar pontos específicos e incluir desenhos de arquitetura diretamente no README (não apenas links).
2. **Integração de Desenhos**: Incorporar diagramas de arquitetura no README para visualização imediata.

### Tecnologias Envolvidas
- Markdown (documentação)
- Ferramentas de diagrama (Draw.io, etc.)

### Cronograma Estimado
- 1 semana
- Paralelo às outras melhorias da Fase 3

### Responsáveis
- Tech Writer/Documentador (ou desenvolvedor responsável por docs)

## Fase 4 - Expansão e Otimização

### Descrição
Após consolidar as melhorias da Fase 3, expandir a aplicação com novas funcionalidades e otimizar performance.

### Sugestões de Evolução
1. **Novas Funcionalidades**: Integração com sistemas externos (ex.: API de veículos, pagamentos), dashboard para clientes, notificações push.
2. **Otimização de Performance**: Cache (Redis), otimização de queries JPA, monitoramento avançado (Prometheus/Grafana).
3. **Segurança Avançada**: Implementar OAuth2, rate limiting, auditoria de logs.
4. **Escalabilidade**: Microserviços, event-driven architecture.

### Tecnologias Envolvidas
- Redis, Prometheus, Grafana
- OAuth2, Kafka (para eventos)
- Microserviços (Spring Cloud)

### Cronograma Estimado
- 4-6 semanas (dependendo da complexidade)
- Fase incremental após Fase 3

### Responsáveis
- Equipe completa (desenvolvimento, DevOps, QA)

## Conclusão

Este plano de fases visa transformar a aplicação em uma solução ainda mais robusta, aderente e escalável. A Fase 3 foca em correções críticas para alinhamento com o enunciado, enquanto a Fase 4 abre caminho para inovações futuras. Recomenda-se priorizar a Fase 3 antes de avançar para expansões.
