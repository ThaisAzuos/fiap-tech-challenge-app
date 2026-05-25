package com.grupo37.oficinamecanica.cadastro.repository;

import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<VeiculoEntity, String> {

    @Query("SELECT v FROM VeiculoEntity v WHERE v.dono.cpf = :cpf")
    List<VeiculoEntity> findByDonoCpf(@Param("cpf") String cpf);
}
