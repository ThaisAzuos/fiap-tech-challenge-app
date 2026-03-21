package com.grupo51.oficina.cleanmvp.application.port;

import com.grupo51.oficina.cleanmvp.domain.OrdemServicoEntity;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoGateway {
    OrdemServicoEntity salvar(OrdemServicoEntity entity);

    Optional<OrdemServicoEntity> buscarPorId(UUID id);
}

