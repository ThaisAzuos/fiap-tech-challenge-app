package com.grupo51.oficinamecanica.cadastro.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mecanicos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mecanico {

    @Id
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especialidade especialidade;

    @Column(unique = true, nullable = false)
    private String registroFuncional;

    private boolean ativo = true;


    public Mecanico(String nome, Cpf cpf, Especialidade especialidade, String registroFuncional) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório");
        if (cpf == null) throw new IllegalArgumentException("CPF é obrigatório");
        if (especialidade == null) throw new IllegalArgumentException("Especialidade é obrigatória");
        if (registroFuncional == null || registroFuncional.isBlank()) throw new IllegalArgumentException("Registro funcional é obrigatório");

        this.cpf = cpf.numero(); // Extrai a String validada do Value Object
        this.nome = nome;
        this.especialidade = especialidade;
        this.registroFuncional = registroFuncional;
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}