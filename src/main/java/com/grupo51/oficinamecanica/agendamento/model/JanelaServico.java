package com.grupo51.oficinamecanica.agendamento.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JanelaServico {
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
}
