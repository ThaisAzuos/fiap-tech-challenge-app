package com.grupo37.oficinamecanica.cadastro.infrastructure.repository;

import com.grupo37.oficinamecanica.cadastro.domain.model.Funcionario;
import com.grupo37.oficinamecanica.cadastro.domain.model.Cargo;
import com.grupo37.oficinamecanica.cadastro.domain.model.Especialidade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "funcionarios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FuncionarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;

    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;

    // Construtor para criar entidade a partir do domínio
    public FuncionarioEntity(Funcionario domain) {
        this.id = domain.getId();
        this.cpf = domain.getCpf();
        this.nome = domain.getNome();
        this.email = domain.getEmail();
        this.cargo = domain.getCargo();
        this.especialidade = domain.getEspecialidade();
    }

    // Método para converter entidade para domínio
    public Funcionario toDomain() {
        return new Funcionario(
            this.id,
            this.cpf,
            this.nome,
            this.email,
            this.cargo,
            this.especialidade
        );
    }
}
