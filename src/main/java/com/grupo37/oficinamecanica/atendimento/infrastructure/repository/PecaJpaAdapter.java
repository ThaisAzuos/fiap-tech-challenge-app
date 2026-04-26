package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.application.port.out.PecaPort;
import com.grupo37.oficinamecanica.estoque.model.Peca;
import com.grupo37.oficinamecanica.estoque.repository.PecaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PecaJpaAdapter implements PecaPort {

    private final PecaRepository pecaRepository;

    public PecaJpaAdapter(PecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    @Override
    public Optional<Peca> findById(UUID id) {
        return pecaRepository.findById(id);
    }
}

