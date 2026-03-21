package com.grupo51.oficinamecanica.atendimento.application.port.out;

import com.grupo51.oficinamecanica.atendimento.domain.model.OrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoPort {
    Optional<OrdemServico> findById(UUID id);

    Optional<OrdemServico> findByIdWithDetails(UUID id);

    Page<OrdemServico> findAllAtivas(Pageable pageable);

    OrdemServico save(OrdemServico ordemServico);
}

