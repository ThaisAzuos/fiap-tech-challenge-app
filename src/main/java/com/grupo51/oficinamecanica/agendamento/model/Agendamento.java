package com.grupo51.oficinamecanica.agendamento.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "agendamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Referência ao ID do Cliente e Veículo (Contexto de Cadastro)
    private String clienteId;
    private String veiculoId;

    @Embedded
    private JanelaServico janela;

    @Enumerated(EnumType.STRING)
    private TipoAgendamento tipo; // ANALISE ou EXECUCAO

    private String recursoId; // ID do Mecânico ou Box

    private boolean confirmado = false;
}


