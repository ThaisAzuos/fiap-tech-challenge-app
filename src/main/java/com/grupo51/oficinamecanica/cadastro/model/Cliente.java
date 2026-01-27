package com.grupo51.oficinamecanica.cadastro.model;

import com.grupo51.oficinamecanica.cadastro.model.dto.EnderecoDTO;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "clientes")
@Getter
public class Cliente {
    @Id
    private String cpf; // ID natural e validado

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Embedded
    private Endereco endereco;

    public Cliente(String nome, String cpf, String email, String telefone, EnderecoDTO endereco) {}

    public Cliente(String nome, Cpf cpf, Email email, String telefone, Endereco endereco) {
        this.cpf = cpf.numero();
        this.email = email.email();
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
    }


}
