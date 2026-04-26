package com.grupo37.oficinamecanica.cadastro.repository;

import com.grupo37.oficinamecanica.cadastro.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, String> {
    
    @Query("SELECT v FROM Veiculo v WHERE v.dono.cpf = :cpf")
    List<Veiculo> findByDonoCpf(@Param("cpf") String cpf);
}
