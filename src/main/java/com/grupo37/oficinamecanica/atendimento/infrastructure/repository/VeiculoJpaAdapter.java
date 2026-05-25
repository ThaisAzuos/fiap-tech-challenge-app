package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.application.port.out.VeiculoPort;
import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.VeiculoEntity;
import com.grupo37.oficinamecanica.cadastro.repository.VeiculoRepository;
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
        return veiculoRepository.findById(placa)
                .map(VeiculoEntity::toDomain); // converte para domínio
    }
}