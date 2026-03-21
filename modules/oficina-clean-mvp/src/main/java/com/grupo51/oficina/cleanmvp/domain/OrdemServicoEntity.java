package com.grupo51.oficina.cleanmvp.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class OrdemServicoEntity {
    private final UUID id;
    private final String placa;
    private final String descricaoProblema;
    private final LocalDateTime dataAbertura;
    private StatusOrdemServico status;

    public OrdemServicoEntity(UUID id, String placa, String descricaoProblema, LocalDateTime dataAbertura) {
        this.id = Objects.requireNonNull(id, "id obrigatorio");
        this.placa = validarTexto(placa, "placa");
        this.descricaoProblema = validarTexto(descricaoProblema, "descricaoProblema");
        this.dataAbertura = Objects.requireNonNull(dataAbertura, "dataAbertura obrigatoria");
        this.status = StatusOrdemServico.RECEBIDA;
    }

    public UUID getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void cancelar() {
        if (status == StatusOrdemServico.ENTREGUE || status == StatusOrdemServico.CANCELADA) {
            throw new IllegalStateException("OS em estado terminal nao pode ser cancelada");
        }
        status = StatusOrdemServico.CANCELADA;
    }

    private static String validarTexto(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " obrigatorio");
        }
        return value;
    }
}

