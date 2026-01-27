package com.grupo51.oficinamecanica.atendimento.repository;

import com.grupo51.oficinamecanica.estoque.model.Peca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PecaRepository extends JpaRepository<Peca, UUID> {
}
