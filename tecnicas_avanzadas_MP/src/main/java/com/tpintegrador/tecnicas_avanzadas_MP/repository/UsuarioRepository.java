package com.tpintegrador.tecnicas_avanzadas_MP.repository;

import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
