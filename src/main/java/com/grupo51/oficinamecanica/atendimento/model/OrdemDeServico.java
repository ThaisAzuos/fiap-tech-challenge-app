package com.grupo51.oficinamecanica.atendimento.model;

import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import com.grupo51.oficinamecanica.estoque.model.Peca;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "ordens_servico")
@Getter
public class OrdemDeServico {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // O Hibernate cuidará da geração do UUID
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String cpfCliente;   // Referência ao ID do Cliente
    private String placaVeiculo; // Referência ao ID do Veículo

    @Enumerated(EnumType.STRING)
    private StatusOS status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "os_id")
    private List<ItemOS> itens = new ArrayList<>();

    protected OrdemDeServico() {}

    public OrdemDeServico(String cpfCliente, String placaVeiculo) {
        this.cpfCliente = cpfCliente;
        this.placaVeiculo = placaVeiculo;
        this.status = StatusOS.RECEBIDA;
    }

    // Método de Domínio: Adicionar item validando o estado da OS
    public void adicionarPeca(Peca peca, int quantidade) {
        if (this.status == StatusOS.FINALIZADA || this.status == StatusOS.ENTREGUE) {
            throw new BusinessException("Não é possível alterar uma OS já encerrada.");
        }

        ItemOS novoItem = new ItemOS(peca.getId(), peca.getNome(), quantidade, peca.getPreco());
        this.itens.add(novoItem);
    }

}