package com.grupo37.oficinamecanica.cadastro.domain.model;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "especialidade", nullable = true)
    private Especialidade especialidade;

    @Column(name = "registro_funcional", unique = true, nullable = true)
    private String registroFuncional;

    private boolean ativo = true;

    // Construtor para todos os funcionários
    public Funcionario(String nome, Cpf cpf, Email email, Cargo cargo, String senha, Especialidade especialidade, String registroFuncional) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (cpf == null) throw new IllegalArgumentException("CPF é obrigatório");
        if (email == null) throw new IllegalArgumentException("E-mail é obrigatório");
        if (cargo == null) throw new IllegalArgumentException("Cargo é obrigatório");
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("Senha é obrigatória");

        if (cargo == Cargo.MECANICO) {
            if (especialidade == null) throw new IllegalArgumentException("Especialidade é obrigatória para mecânicos");
            if (registroFuncional == null || registroFuncional.isBlank()) throw new IllegalArgumentException("Registro funcional é obrigatório para mecânicos");
        }

        this.cpf = cpf.numero();
        this.nome = nome;
        this.email = email.endereco();
        this.cargo = cargo;
        this.senha = senha;
        this.especialidade = especialidade;
        this.registroFuncional = registroFuncional;
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}