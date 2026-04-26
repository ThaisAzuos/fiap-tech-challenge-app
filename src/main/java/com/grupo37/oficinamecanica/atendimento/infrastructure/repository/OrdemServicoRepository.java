package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository extends JpaRepository<OrdemServicoEntity, UUID> {

    @Query("SELECT DISTINCT os FROM OrdemServicoEntity os LEFT JOIN FETCH os.itens LEFT JOIN FETCH os.servicos LEFT JOIN FETCH os.veiculo v LEFT JOIN FETCH v.dono WHERE os.id = :id")
    Optional<OrdemServicoEntity> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT os FROM OrdemServicoEntity os JOIN FETCH os.veiculo v JOIN FETCH v.dono WHERE os.status NOT IN ('FINALIZADA', 'ENTREGUE') ORDER BY os.status, os.dataAbertura")
    Page<OrdemServicoEntity> findAllAtivas(Pageable pageable);
}
