package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.application.port.out.OrdemServicoPort;
import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.VeiculoEntity;
import com.grupo37.oficinamecanica.cadastro.repository.VeiculoRepository;
import com.grupo37.oficinamecanica.comum.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class OrdemServicoJpaAdapter implements OrdemServicoPort {

    private final OrdemServicoRepository ordemServicoRepository;
    private final VeiculoRepository veiculoRepository;

    public OrdemServicoJpaAdapter(OrdemServicoRepository ordemServicoRepository, VeiculoRepository veiculoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public Optional<OrdemServico> findById(UUID id) {
        return ordemServicoRepository.findById(id)
                .map(OrdemServicoEntity::toDomain);
    }

    @Override
    public Optional<OrdemServico> findByIdWithDetails(UUID id) {
        return ordemServicoRepository.findByIdWithDetails(id)
                .map(OrdemServicoEntity::toDomain);
    }

    @Override
    public Page<OrdemServico> findAllAtivas(Pageable pageable) {
        return ordemServicoRepository.findAllAtivas(pageable)
                .map(OrdemServicoEntity::toDomain);
    }

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        VeiculoEntity veiculoEntity = veiculoRepository.findById(ordemServico.getVeiculo().getPlaca())
                .orElseThrow(() -> new BusinessException("Veículo não encontrado"));
        OrdemServicoEntity entity = new OrdemServicoEntity(ordemServico, veiculoEntity);
        return ordemServicoRepository.save(entity).toDomain();
    }
}
