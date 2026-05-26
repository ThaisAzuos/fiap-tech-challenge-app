package com.grupo37.oficinamecanica.cadastro.infrastructure.repository;

import com.grupo37.oficinamecanica.cadastro.domain.model.Funcionario;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cargo;
import com.grupo37.oficinamecanica.cadastro.domain.model.Especialidade;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cpf;
import com.grupo37.oficinamecanica.cadastro.domain.model.Email;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funcionarios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FuncionarioEntity {

    @Id
    @Column(nullable = false, length = 11)
    private String cpf; // PK natural, alinhado com a migration e o FuncionarioRepository

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;

    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;

    @Column(name = "registro_funcional", unique = true)
    private String registroFuncional;

    @Column(nullable = false)
    private boolean ativo;

    // Construtor para criar entidade a partir do domínio
    public FuncionarioEntity(Funcionario domain) {
        this.cpf = domain.getCpf();
        this.nome = domain.getNome();
        this.email = domain.getEmail();
        this.cargo = domain.getCargo();
        this.especialidade = domain.getEspecialidade();
        this.senha = domain.getSenha();
        this.registroFuncional = domain.getRegistroFuncional();
        this.ativo = domain.isAtivo();
    }

    // Método para converter entidade para domínio
    public Funcionario toDomain() {
        return new Funcionario(
                this.nome,
                new Cpf(this.cpf),
                new Email(this.email),
                this.cargo,
                this.senha,
                this.especialidade,
                this.registroFuncional
        );
    }
}