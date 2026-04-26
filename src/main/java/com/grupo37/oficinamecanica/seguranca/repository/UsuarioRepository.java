package com.grupo37.oficinamecanica.seguranca.repository;

import com.grupo37.oficinamecanica.seguranca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByLogin(String login);
}
