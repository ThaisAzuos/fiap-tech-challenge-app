package com.grupo37.oficinamecanica.atendimento.application.port.out;

import com.grupo37.oficinamecanica.atendimento.domain.model.Servico;

import java.util.Optional;
import java.util.UUID;

public interface ServicoPort {
    Optional<Servico> findById(UUID id);
    Servico save(Servico servico);
}
