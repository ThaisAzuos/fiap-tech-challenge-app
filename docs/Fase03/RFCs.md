# Request for Comments (RFCs)

## RFC-001: Estratégia de CI/CD
- **Proposta**: Utilizar GitHub Actions para pipelines.
- **Detalhes**:
  - Branch main protegida.
  - Deploy automático para homologação e produção.
  - Uso de Pull Requests obrigatório.
- **Discussão**:
  - Alternativas: GitLab CI.
  - Vantagens: Integração com GitHub, comunidade ativa.

## RFC-002: Observabilidade
- **Proposta**: Adotar Datadog para métricas, logs e alertas.
- **Detalhes**:
  - Dashboards para ordens de serviço.
  - Alertas configurados para falhas.
  - Logs estruturados em JSON.
- **Discussão**:
  - Alternativas: New Relic.
  - Vantagens: Integração com AWS e Kubernetes.
