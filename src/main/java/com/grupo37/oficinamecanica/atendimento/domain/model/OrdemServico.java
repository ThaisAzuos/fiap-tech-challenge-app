package com.grupo37.oficinamecanica.atendimento.domain.model;

import com.grupo37.oficinamecanica.cadastro.model.Veiculo;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import com.grupo37.oficinamecanica.estoque.model.Peca;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class OrdemServico {

    private UUID id;
    private Veiculo veiculo;
    private String descricaoProblema;
    private LocalDateTime dataAbertura;
    private StatusOS status;
    private List<ItemOS> itens = new ArrayList<>();
    private List<ItemServicoOS> servicos = new ArrayList<>();
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private LocalDateTime dataCancelamento;
    private String motivoCancelamento;
    private LocalDateTime dataConclusao;

    // Construtor de domínio
    public OrdemServico(Veiculo veiculo, String descricaoProblema) {
        this.veiculo = veiculo;
        this.descricaoProblema = descricaoProblema;
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusOS.RECEBIDA;
        this.valorTotal = BigDecimal.ZERO;
    }

    // Construtor completo para infraestrutura
    public OrdemServico(UUID id, Veiculo veiculo, String descricaoProblema, LocalDateTime dataAbertura,
                       StatusOS status, List<ItemOS> itens, List<ItemServicoOS> servicos, BigDecimal valorTotal,
                       LocalDateTime dataCancelamento, String motivoCancelamento, LocalDateTime dataConclusao) {
        this.id = id;
        this.veiculo = veiculo;
        this.descricaoProblema = descricaoProblema;
        this.dataAbertura = dataAbertura;
        this.status = status;
        this.itens = itens != null ? itens : new ArrayList<>();
        this.servicos = servicos != null ? servicos : new ArrayList<>();
        this.valorTotal = valorTotal != null ? valorTotal : BigDecimal.ZERO;
        this.dataCancelamento = dataCancelamento;
        this.motivoCancelamento = motivoCancelamento;
        this.dataConclusao = dataConclusao;
    }

    // Getters
    public UUID getId() { return id; }
    public Veiculo getVeiculo() { return veiculo; }
    public String getDescricaoProblema() { return descricaoProblema; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public StatusOS getStatus() { return status; }
    public List<ItemOS> getItens() { return itens; }
    public List<ItemServicoOS> getServicos() { return servicos; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public LocalDateTime getDataCancelamento() { return dataCancelamento; }
    public String getMotivoCancelamento() { return motivoCancelamento; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }

    // Métodos de negócio
    public void adicionarPeca(Peca peca, int quantidade) {
        if (this.status == StatusOS.FINALIZADA || this.status == StatusOS.ENTREGUE || this.status == StatusOS.CANCELADA) {
            throw new BusinessException("Não é possível alterar uma OS já encerrada (" + this.status + ").");
        }

        ItemOS novoItem = new ItemOS(peca, quantidade);
        this.itens.add(novoItem);

        BigDecimal valorItem = peca.getPreco().multiply(BigDecimal.valueOf(quantidade));
        this.valorTotal = this.valorTotal.add(valorItem);
    }

    public void adicionarServico(Servico servico) {
        if (this.status == StatusOS.FINALIZADA || this.status == StatusOS.ENTREGUE || this.status == StatusOS.CANCELADA) {
            throw new BusinessException("Não é possível alterar uma OS já encerrada (" + this.status + ").");
        }

        ItemServicoOS novoServico = new ItemServicoOS(servico);
        this.servicos.add(novoServico);

        this.valorTotal = this.valorTotal.add(servico.getPreco());
    }

    public void atualizarStatus(StatusOS novoStatus) {
        // 1. Validar estados terminais
        if (this.status == StatusOS.ENTREGUE) {
            throw new BusinessException("Esta ordem de serviço já foi ENTREGUE e não pode mais ser alterada.");
        }
        if (this.status == StatusOS.CANCELADA) {
            throw new BusinessException("Esta ordem de serviço já foi CANCELADA e não pode mais ser alterada.");
        }

        // 2. Permitir cancelamento em qualquer status (exceto já cancelada/entregue)
        if (novoStatus == StatusOS.CANCELADA) {
            this.status = novoStatus;
            this.dataCancelamento = LocalDateTime.now();
            return;
        }

        // 3. Regra de negócio: não pode pular do RECEBIDA direto para FINALIZADA
        if (this.status == StatusOS.RECEBIDA &&
                (novoStatus == StatusOS.FINALIZADA || novoStatus == StatusOS.ENTREGUE)) {
            throw new BusinessException("A OS precisa passar por diagnóstico antes de ser finalizada ou entregue.");
        }

        // 4. Permitir retrocesso apenas de AGUARDANDO_APROVACAO para EM_DIAGNOSTICO (rejeição de orçamento)
        if (this.status == StatusOS.AGUARDANDO_APROVACAO && novoStatus == StatusOS.EM_DIAGNOSTICO) {
            this.status = novoStatus;
            return;
        }

        // 5. Bloqueio de retrocesso para outros casos
        if (novoStatus.ordinal() < this.status.ordinal()) {
            throw new BusinessException(
                String.format("Não é permitido retornar a Ordem de Serviço do status %s para %s.", this.status, novoStatus)
            );
        }

        this.status = novoStatus;

        // Registrar data de conclusão
        if (novoStatus == StatusOS.FINALIZADA) {
            this.dataConclusao = LocalDateTime.now();
        }
    }

    /**
     * Cancela a ordem de serviço com motivo
     * @param motivo Descrição do motivo do cancelamento
     */
    public void cancelar(String motivo) {
        if (this.status == StatusOS.ENTREGUE || this.status == StatusOS.CANCELADA) {
            throw new BusinessException("Esta ordem de serviço já está encerrada (" + this.status + ") e não pode ser cancelada novamente.");
        }
        this.status = StatusOS.CANCELADA;
        this.motivoCancelamento = motivo;
        this.dataCancelamento = LocalDateTime.now();
    }
}
