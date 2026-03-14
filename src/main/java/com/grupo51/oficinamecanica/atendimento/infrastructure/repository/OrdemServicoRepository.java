package com.grupo51.oficinamecanica.atendimento.infrastructure.repository;


import com.grupo51.oficinamecanica.atendimento.domain.model.OrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, UUID> {

    @Query("SELECT os FROM OrdemServico os JOIN FETCH os.itens JOIN FETCH os.veiculo v JOIN FETCH v.dono WHERE os.id = :id")
    Optional<OrdemServico> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT os FROM OrdemServico os JOIN FETCH os.veiculo v JOIN FETCH v.dono WHERE os.status NOT IN ('FINALIZADA', 'ENTREGUE')")
    Page<OrdemServico> findAllAtivas(Pageable pageable);
}
