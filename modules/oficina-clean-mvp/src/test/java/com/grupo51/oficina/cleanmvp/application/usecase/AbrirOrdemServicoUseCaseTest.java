package com.grupo51.oficina.cleanmvp.application.usecase;

import com.grupo51.oficina.cleanmvp.domain.OrdemServicoEntity;
import com.grupo51.oficina.cleanmvp.domain.StatusOrdemServico;
import com.grupo51.oficina.cleanmvp.infrastructure.gateway.InMemoryOrdemServicoGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AbrirOrdemServicoUseCaseTest {

    @Test
    void deveCriarOrdemServicoComStatusRecebida() {
        InMemoryOrdemServicoGateway gateway = new InMemoryOrdemServicoGateway();
        AbrirOrdemServicoUseCase useCase = new AbrirOrdemServicoUseCase(gateway);

        OrdemServicoEntity ordem = useCase.executar(new AbrirOrdemServicoCommand("ABC1D23", "Ruido na suspensao"));

        assertNotNull(ordem.getId());
        assertEquals("ABC1D23", ordem.getPlaca());
        assertEquals(StatusOrdemServico.RECEBIDA, ordem.getStatus());
    }
}

