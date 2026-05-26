package com.grupo37.oficinamecanica.cadastro.domain.model;

import java.util.UUID;

public class Cliente {

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
    public Cliente(String cpf, String nome, String email, String telefone, Endereco endereco) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    // Getters
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public Endereco getEndereco() { return endereco; }
}
