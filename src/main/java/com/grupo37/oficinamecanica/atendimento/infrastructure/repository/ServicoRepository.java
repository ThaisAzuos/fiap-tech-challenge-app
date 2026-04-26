package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServicoRepository extends JpaRepository<ServicoEntity, UUID> {
}
