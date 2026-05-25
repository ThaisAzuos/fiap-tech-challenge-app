package com.grupo37.oficinamecanica.cadastro.repository;


import com.grupo37.oficinamecanica.cadastro.infrastructure.repository.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<ClienteEntity, String> {
}
