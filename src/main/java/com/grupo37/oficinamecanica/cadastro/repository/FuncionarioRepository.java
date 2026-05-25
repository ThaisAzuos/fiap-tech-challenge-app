package com.grupo37.oficinamecanica.cadastro.repository;

import com.grupo37.oficinamecanica.cadastro.domain.model.Cargo;
import com.grupo37.oficinamecanica.cadastro.domain.model.Especialidade;
import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionarioRepository extends JpaRepository<FuncionarioEntity, String> {

    boolean existsByEmail(String email);

    List<FuncionarioEntity> findByCargo(Cargo cargo);

    List<FuncionarioEntity> findByEspecialidadeAndAtivoTrue(Especialidade especialidade);

    List<FuncionarioEntity> findAllByAtivoTrue();
}