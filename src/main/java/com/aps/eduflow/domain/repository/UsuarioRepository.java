package com.aps.eduflow.domain.repository;

import com.aps.eduflow.domain.entity.Usuario;
import com.aps.eduflow.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByRole(UserRole role);

    boolean existsByEmail(String email);

    boolean existsByMatricula(String matricula);
}
