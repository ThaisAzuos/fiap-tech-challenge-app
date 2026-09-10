package com.grupo37.oficinamecanica.atendimento.infrastructure.messaging.events;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload do evento OrdemServicoCriada (routing key os.criada).
 *
 * Correção em relação ao contrato original desenhado no Dia 1
 * (docs/arquitetura/eventos/ordem-servico-criada.schema.json): o domínio
 * Cliente/Veiculo desta aplicação não possui um id UUID próprio — Cliente é
 * identificado por CPF e Veiculo por placa (chaves naturais). O schema foi
 * atualizado no Dia 3 para refletir isso (clienteCpf/veiculoPlaca no lugar
 * de clienteId/veiculoId). Ver ADR-009.
 */
public record OrdemServicoCriadaPayload(
        String clienteCpf,
        String clienteNome,
        String clienteEmail,
        String veiculoPlaca,
        List<ItemPrevisto> itensPrevistos,
        BigDecimal valorTotalPrevisto,
        String status
) {
    public record ItemPrevisto(String descricao, String tipo) {
    }
}
