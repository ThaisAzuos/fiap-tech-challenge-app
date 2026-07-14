# language: pt
Funcionalidade: Saga da Ordem de Serviço (orquestrada pelo OS Service)

  A Saga coordena orçamento (Billing Service), pagamento (Billing Service) e
  execução (Execution Service) de uma Ordem de Serviço, com o OS Service como
  orquestrador (ver docs/arquitetura/fase4-visao-geral.md e ADR-009).

  Cenário: Fluxo feliz completo — orçamento aprovado, pagamento confirmado e execução concluída
    Dado uma Ordem de Serviço "os-fluxo-feliz"
    Quando o orçamento da Ordem de Serviço é aprovado
    Então a Ordem de Serviço é aprovada e avança para execução
    Quando o pagamento da Ordem de Serviço é confirmado
    Então nenhuma transição adicional de status é realizada
    Quando a execução da Ordem de Serviço é concluída
    Então a Ordem de Serviço é finalizada
    E nenhum evento de compensação é publicado

  Cenário: Orçamento reprovado — compensação da Saga (rollback)
    Dado uma Ordem de Serviço "os-orcamento-reprovado"
    Quando o orçamento da Ordem de Serviço é reprovado com o motivo "Cliente não aprovou o valor"
    Então a Ordem de Serviço é cancelada com o motivo "Cliente não aprovou o valor"
    E um evento de compensação da Saga é publicado para a etapa "ORCAMENTO"

  Cenário: Pagamento falha após orçamento aprovado — compensação da Saga (rollback)
    Dado uma Ordem de Serviço "os-pagamento-falhou"
    Quando o orçamento da Ordem de Serviço é aprovado
    E o pagamento da Ordem de Serviço falha com o motivo "Pagamento recusado pela operadora"
    Então a Ordem de Serviço é cancelada com o motivo "Pagamento recusado pela operadora"
    E um evento de compensação da Saga é publicado para a etapa "PAGAMENTO"
