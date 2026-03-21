package com.grupo51.oficina.cleanmvp.application.usecase;

import com.grupo51.oficina.cleanmvp.application.port.OrdemServicoGateway;
import com.grupo51.oficina.cleanmvp.domain.OrdemServicoEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class AbrirOrdemServicoUseCase {
    private final OrdemServicoGateway gateway;

    public AbrirOrdemServicoUseCase(OrdemServicoGateway gateway) {
        this.gateway = gateway;
    }

    public OrdemServicoEntity executar(AbrirOrdemServicoCommand command) {
        OrdemServicoEntity entity = new OrdemServicoEntity(
                UUID.randomUUID(),
                command.placa(),
                command.descricaoProblema(),
                LocalDateTime.now()
        );
        return gateway.salvar(entity);
    }
}

