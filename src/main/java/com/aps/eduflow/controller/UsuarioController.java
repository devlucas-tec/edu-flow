package com.aps.eduflow.controller;

import com.aps.eduflow.domain.dto.CadastroUsuarioRequest;
import com.aps.eduflow.domain.entity.Usuario;
import com.aps.eduflow.domain.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/usuarios/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody @Valid CadastroUsuarioRequest dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Já existe um usuário com esse e-mail");
        }

        if (usuarioRepository.existsByMatricula(dto.getMatricula())) {
            return ResponseEntity.badRequest().body("Já existe um usuário com essa matrícula");
        }

        Usuario usuario = new Usuario(
                null,
                dto.getNome(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getSenha()),
                dto.getMatricula(),
                dto.getRole(),
                null
        );

        usuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(new UsuarioResponse(usuario));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    private record UsuarioResponse(Long id, String nome, String email, String matricula, String role) {
        UsuarioResponse(Usuario u) {
            this(u.getId(), u.getNome(), u.getEmail(), u.getMatricula(), u.getRole().name());
        }
    }
}
