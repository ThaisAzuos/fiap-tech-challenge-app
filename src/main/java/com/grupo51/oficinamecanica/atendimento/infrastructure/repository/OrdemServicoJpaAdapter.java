package com.grupo51.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo51.oficinamecanica.atendimento.application.port.out.OrdemServicoPort;
import com.grupo51.oficinamecanica.atendimento.domain.model.OrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class OrdemServicoJpaAdapter implements OrdemServicoPort {

    private final OrdemServicoRepository ordemServicoRepository;

    public OrdemServicoJpaAdapter(OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Override
    public Optional<OrdemServico> findById(UUID id) {
        return ordemServicoRepository.findById(id);
    }

    @Override
    public Optional<OrdemServico> findByIdWithDetails(UUID id) {
        return ordemServicoRepository.findByIdWithDetails(id);
    }

    @Override
    public Page<OrdemServico> findAllAtivas(Pageable pageable) {
        return ordemServicoRepository.findAllAtivas(pageable);
    }

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        return ordemServicoRepository.save(ordemServico);
    }
}

