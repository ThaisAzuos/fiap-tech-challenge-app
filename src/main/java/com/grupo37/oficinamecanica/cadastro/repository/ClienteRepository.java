package com.grupo37.oficinamecanica.cadastro.repository;

import com.grupo37.oficinamecanica.cadastro.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
}
