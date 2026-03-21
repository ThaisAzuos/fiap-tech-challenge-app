package com.grupo51.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo51.oficinamecanica.atendimento.application.port.out.VeiculoPort;
import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.cadastro.repository.VeiculoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VeiculoJpaAdapter implements VeiculoPort {

    private final VeiculoRepository veiculoRepository;

    public VeiculoJpaAdapter(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public Optional<Veiculo> findById(String placa) {
        return veiculoRepository.findById(placa);
    }
}

