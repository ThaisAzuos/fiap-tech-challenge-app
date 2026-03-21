package com.grupo51.oficinamecanica.atendimento.application.port.out;

import com.grupo51.oficinamecanica.estoque.model.Peca;

import java.util.Optional;
import java.util.UUID;

public interface PecaPort {
    Optional<Peca> findById(UUID id);
}

