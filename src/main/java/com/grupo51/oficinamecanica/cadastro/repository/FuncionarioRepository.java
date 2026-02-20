package com.grupo51.oficinamecanica.cadastro.repository;

import com.grupo51.oficinamecanica.cadastro.model.Cargo;
import com.grupo51.oficinamecanica.cadastro.model.Especialidade;
import com.grupo51.oficinamecanica.cadastro.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {

    // Útil para segurança: verificar se o e-mail já existe
    boolean existsByEmail(String email);

    // Útil para RH: listar por cargo
    List<Funcionario> findByCargo(Cargo cargo);

    // Útil para o Agendamento: buscar mecânicos por especialidade
    List<Funcionario> findByEspecialidadeAndAtivoTrue(Especialidade especialidade);

    // Útil para listagens: buscar apenas os ativos
    List<Funcionario> findAllByAtivoTrue();
}
