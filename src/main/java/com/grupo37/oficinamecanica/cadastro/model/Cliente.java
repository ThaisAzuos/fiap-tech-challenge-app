package com.grupo37.oficinamecanica.cadastro.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cliente {
    @Id
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Embedded
    private Endereco endereco;

    public Cliente(String nome, Cpf cpf, Email email, String telefone, Endereco endereco) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");

        this.cpf = cpf.numero();
        this.email = email.endereco();
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
    }
}
