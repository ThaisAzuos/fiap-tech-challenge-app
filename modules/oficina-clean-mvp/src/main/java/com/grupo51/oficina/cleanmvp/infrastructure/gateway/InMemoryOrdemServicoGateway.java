package com.grupo51.oficina.cleanmvp.infrastructure.gateway;

import com.grupo51.oficina.cleanmvp.application.port.OrdemServicoGateway;
import com.grupo51.oficina.cleanmvp.domain.OrdemServicoEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrdemServicoGateway implements OrdemServicoGateway {
    private final Map<UUID, OrdemServicoEntity> storage = new ConcurrentHashMap<>();

    @Override
    public OrdemServicoEntity salvar(OrdemServicoEntity entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<OrdemServicoEntity> buscarPorId(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }
}

