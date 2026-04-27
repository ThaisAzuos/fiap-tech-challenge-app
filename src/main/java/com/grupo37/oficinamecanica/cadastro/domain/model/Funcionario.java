package com.grupo37.oficinamecanica.cadastro.domain.model;

import java.util.UUID;

public class Funcionario {

    private UUID id;
    private String cpf;
    private String nome;
    private String email;
    private Cargo cargo;
    private Especialidade especialidade;

    // Construtor de domínio
    public Funcionario(String nome, Cpf cpf, Email email, Cargo cargo, Especialidade especialidade) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (cargo == null) throw new IllegalArgumentException("Cargo é obrigatório");

        this.cpf = cpf.numero();
        this.email = email.endereco();
        this.nome = nome;
        this.cargo = cargo;
        this.especialidade = especialidade;
    }

    // Construtor completo para infraestrutura
    public Funcionario(UUID id, String cpf, String nome, String email, Cargo cargo, Especialidade especialidade) {
        this.id = id;
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.especialidade = especialidade;
    }

    // Getters
    public UUID getId() { return id; }
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Cargo getCargo() { return cargo; }
    public Especialidade getEspecialidade() { return especialidade; }
}
