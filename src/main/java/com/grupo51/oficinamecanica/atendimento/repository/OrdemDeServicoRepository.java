package com.grupo51.oficinamecanica.atendimento.repository;

import com.grupo51.oficinamecanica.atendimento.model.OrdemDeServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrdemDeServicoRepository extends JpaRepository<OrdemDeServico, UUID> {
}
