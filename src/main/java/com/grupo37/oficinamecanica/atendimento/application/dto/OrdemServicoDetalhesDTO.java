package com.grupo37.oficinamecanica.atendimento.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrdemServicoDetalhesDTO(
        UUID id,
        String status,
        String placaVeiculo,
        String modeloVeiculo,
        String nomeCliente,
        String descricaoProblema,
        List<ItemOSDTO> itens,
        List<ItemServicoOSDTO> servicos,
        BigDecimal valorTotal
) {
    public record ItemOSDTO(
            String nomePeca,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {}

    public record ItemServicoOSDTO(
            String nomeServico,
            BigDecimal preco
    ) {}
}
