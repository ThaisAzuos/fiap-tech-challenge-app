package com.grupo37.oficinamecanica.cadastro.infrastructure.repository;

import com.grupo37.oficinamecanica.cadastro.domain.model.Cliente;
import com.grupo37.oficinamecanica.cadastro.domain.model.Endereco;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Embedded
    private Endereco endereco;

    // Construtor para criar entidade a partir do domínio
    public ClienteEntity(Cliente domain) {
        this.id = domain.getId();
        this.cpf = domain.getCpf();
        this.nome = domain.getNome();
        this.email = domain.getEmail();
        this.telefone = domain.getTelefone();
        this.endereco = domain.getEndereco();
    }

    // Método para converter entidade para domínio
    public Cliente toDomain() {
        return new Cliente(
            this.id,
            this.cpf,
            this.nome,
            this.email,
            this.telefone,
            this.endereco
        );
    }
}
