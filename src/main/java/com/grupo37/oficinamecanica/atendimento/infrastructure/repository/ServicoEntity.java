package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.domain.model.Servico;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "servicos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(length = 500)
    private String descricao;

    // Construtor para criar entidade a partir do domínio
    public ServicoEntity(Servico domain) {
        this.id = domain.getId();
        this.nome = domain.getNome();
        this.preco = domain.getPreco();
        this.descricao = domain.getDescricao();
    }

    // Método para converter entidade para domínio
    public Servico toDomain() {
        return new Servico(
            this.id,
            this.nome,
            this.preco,
            this.descricao
        );
    }
}
