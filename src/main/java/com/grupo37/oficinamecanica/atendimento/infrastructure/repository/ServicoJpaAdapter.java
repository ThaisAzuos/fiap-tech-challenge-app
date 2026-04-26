package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.application.port.out.ServicoPort;
import com.grupo37.oficinamecanica.atendimento.domain.model.Servico;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ServicoJpaAdapter implements ServicoPort {

    private final ServicoRepository servicoRepository;

    public ServicoJpaAdapter(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Override
    public Optional<Servico> findById(UUID id) {
        return servicoRepository.findById(id)
                .map(ServicoEntity::toDomain);
    }

    @Override
    public Servico save(Servico servico) {
        ServicoEntity entity = new ServicoEntity(servico);
        ServicoEntity savedEntity = servicoRepository.save(entity);
        return savedEntity.toDomain();
    }
}
