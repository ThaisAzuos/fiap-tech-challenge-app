package com.grupo37.oficinamecanica.atendimento.application.port.out;

import com.grupo37.oficinamecanica.estoque.model.Peca;

import java.util.Optional;
import java.util.UUID;

public interface PecaPort {
    Optional<Peca> findById(UUID id);
}

