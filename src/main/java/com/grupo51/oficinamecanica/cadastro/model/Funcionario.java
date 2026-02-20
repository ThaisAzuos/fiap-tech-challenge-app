package com.grupo51.oficinamecanica.cadastro.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funcionarios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Funcionario {

    @Id
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;

    @Column(nullable = false)
    private String senha;

    public Funcionario(String nome, Cpf cpf, Email email, Cargo cargo, String senha) {
        // Validações básicas de domínio (Fail-fast)
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (cpf == null) throw new IllegalArgumentException("CPF é obrigatório");
        if (email == null) throw new IllegalArgumentException("E-mail é obrigatório");
        if (cargo == null) throw new IllegalArgumentException("Cargo é obrigatório");
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("Senha é obrigatória");

        this.cpf = cpf.numero();
        this.nome = nome;
        this.email = email.endereco();
        this.cargo = cargo;
        this.senha = senha;
    }
}
