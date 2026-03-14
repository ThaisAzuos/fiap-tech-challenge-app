package com.grupo51.oficinamecanica.atendimento.repository;


import com.grupo51.oficinamecanica.atendimento.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, UUID> {

    @Query("SELECT os FROM OrdemServico os JOIN FETCH os.itens JOIN FETCH os.veiculo v JOIN FETCH v.dono WHERE os.id = :id")
    Optional<OrdemServico> findByIdWithDetails(@Param("id") UUID id);
}
