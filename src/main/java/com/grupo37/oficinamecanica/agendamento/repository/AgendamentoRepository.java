package com.grupo37.oficinamecanica.agendamento.repository;

import com.grupo37.oficinamecanica.agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    /**
     * Verifica se existe algum agendamento para o recurso que sobreponha o horário solicitado.
     * A lógica de overlap considera: (Inicio1 < Fim2) E (Fim1 > Inicio2)
     */
    @Query("SELECT COUNT(a) > 0 FROM Agendamento a " +
            "WHERE a.recursoId = :recursoId " +
            "AND a.confirmado = true " +
            "AND (:inicio < a.janela.dataHoraFim AND :fim > a.janela.dataHoraInicio)")
    boolean existsByRecursoIdAndJanelaOverlap(
            @Param("recursoId") String recursoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}

