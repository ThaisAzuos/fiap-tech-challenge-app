package com.grupo37.oficinamecanica.estoque.infrastructure.repository;

import com.grupo37.oficinamecanica.estoque.domain.model.Peca;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pecas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PecaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String nome;
    private BigDecimal preco;
    private Integer quantidadeEstoque;

    // Construtor para criar entidade a partir do domínio
    public PecaEntity(Peca domain) {
        this.id = domain.getId();
        this.nome = domain.getNome();
        this.preco = domain.getPreco();
        this.quantidadeEstoque = domain.getQuantidadeEstoque();
    }

    // Método para converter entidade para domínio
    public Peca toDomain() {
        return new Peca(
            this.id,
            this.nome,
            this.preco,
            this.quantidadeEstoque
        );
    }
}
