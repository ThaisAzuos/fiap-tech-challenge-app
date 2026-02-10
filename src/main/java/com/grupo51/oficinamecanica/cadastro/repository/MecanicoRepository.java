package com.grupo51.oficinamecanica.cadastro.repository;

import com.grupo51.oficinamecanica.cadastro.model.Especialidade;
import com.grupo51.oficinamecanica.cadastro.model.Mecanico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MecanicoRepository extends JpaRepository<Mecanico, String> {

    // Útil para o Agendamento: buscar mecânicos por especialidade
    List<Mecanico> findByEspecialidadeAndAtivoTrue(Especialidade especialidade);

    // Útil para listagens: buscar apenas os ativos
    List<Mecanico> findAllByAtivoTrue();
}
