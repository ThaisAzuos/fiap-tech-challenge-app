package com.grupo37.oficinamecanica.estoque.repository;

import com.grupo37.oficinamecanica.estoque.model.Peca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PecaRepository extends JpaRepository<Peca, UUID> {
}
