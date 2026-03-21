package com.grupo51.oficina.cleanmvp.infrastructure.runner;

import com.grupo51.oficina.cleanmvp.application.usecase.AbrirOrdemServicoCommand;
import com.grupo51.oficina.cleanmvp.application.usecase.AbrirOrdemServicoUseCase;
import com.grupo51.oficina.cleanmvp.domain.OrdemServicoEntity;
import com.grupo51.oficina.cleanmvp.infrastructure.gateway.InMemoryOrdemServicoGateway;

public final class CleanMvpRunner {
    private CleanMvpRunner() {
    }

    public static void main(String[] args) {
        InMemoryOrdemServicoGateway gateway = new InMemoryOrdemServicoGateway();
        AbrirOrdemServicoUseCase useCase = new AbrirOrdemServicoUseCase(gateway);

        OrdemServicoEntity os = useCase.executar(new AbrirOrdemServicoCommand("ABC1D23", "Falha na suspensao"));
        System.out.println("OS criada: " + os.getId() + " status=" + os.getStatus());
    }
}

