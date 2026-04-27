package com.grupo37.oficinamecanica.cadastro.domain.model;

import java.util.UUID;

public class Cliente {

    private UUID id;
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private Endereco endereco;

    // Construtor de domínio
    public Cliente(String nome, Cpf cpf, Email email, String telefone, Endereco endereco) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");

        this.cpf = cpf.numero();
        this.email = email.endereco();
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    // Construtor completo para infraestrutura
    public Cliente(UUID id, String cpf, String nome, String email, String telefone, Endereco endereco) {
        this.id = id;
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    // Getters
    public UUID getId() { return id; }
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public Endereco getEndereco() { return endereco; }
}
