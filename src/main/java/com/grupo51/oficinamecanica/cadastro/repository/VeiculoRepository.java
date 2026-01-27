package com.grupo51.oficinamecanica.cadastro.repository;

import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, String> {
    List<Veiculo> findByDonoCpf(String cpf);
}
