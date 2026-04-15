package com.aps.eduflow.domain.dto;

import com.aps.eduflow.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String matricula;
    private String role;

    public UsuarioResponseDTO(Usuario u) {
        this(u.getId(), u.getNome(), u.getEmail(), u.getMatricula(), u.getRole().name());
    }
}
