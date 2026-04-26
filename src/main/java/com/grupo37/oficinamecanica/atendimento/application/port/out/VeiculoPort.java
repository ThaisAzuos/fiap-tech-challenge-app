package com.grupo37.oficinamecanica.atendimento.application.port.out;

import com.grupo37.oficinamecanica.cadastro.model.Veiculo;

import java.util.Optional;

public interface VeiculoPort {
    Optional<Veiculo> findById(String placa);
}

