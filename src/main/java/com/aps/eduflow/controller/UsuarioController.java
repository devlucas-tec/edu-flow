package com.aps.eduflow.controller;

import com.aps.eduflow.domain.dto.CadastroUsuarioRequest;
import com.aps.eduflow.domain.dto.UsuarioResponseDTO;
import com.aps.eduflow.domain.dto.UsuarioUpdateRequestDTO;
import com.aps.eduflow.domain.entity.Usuario;
import com.aps.eduflow.domain.enums.UserRole;
import com.aps.eduflow.domain.exception.RegraNegocioException;
import com.aps.eduflow.domain.repository.UsuarioRepository;
import com.aps.eduflow.infra.security.UserDetailsServiceImpl;
import com.aps.eduflow.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UserDetailsServiceImpl userDetailsService;

    public UsuarioController(UsuarioService usuarioService, UserDetailsServiceImpl userDetailsService) {
        this.usuarioService = usuarioService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Auto-cadastro público — role será sempre ALUNO.
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid CadastroUsuarioRequest dto) {
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/usuarios/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Cadastro com role definido — requer ADMIN ou PROFESSOR.
     */
    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_PROFESSOR"})
    public ResponseEntity<UsuarioResponseDTO> cadastrarComRole(@RequestBody @Valid CadastroUsuarioRequest dto) {
        UserRole roleAutorizado = getRoleAutenticado();
        UsuarioResponseDTO response = usuarioService.cadastrarComRole(dto, roleAutorizado);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/usuarios/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Buscar usuário por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Listar todos os usuários, com filtro opcional por role.
     */
    @GetMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<List<UsuarioResponseDTO>> listar(
            @RequestParam(required = false) UserRole role) {
        List<UsuarioResponseDTO> response = usuarioService.listarTodos(role);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar dados cadastrais — requer ADMIN ou PROFESSOR.
     */
    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_PROFESSOR"})
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioUpdateRequestDTO dto) {
        UserRole roleAutorizado = getRoleAutenticado();
        UsuarioResponseDTO response = usuarioService.atualizar(id, dto, roleAutorizado);
        return ResponseEntity.ok(response);
    }

    /**
     * Promover role de um usuário — ADMIN e PROFESSOR podem promover a MONITOR;
     * somente ADMIN pode criar PROFESSOR.
     */
    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Promover role de um usuário — ADMIN e PROFESSOR podem promover a MONITOR;
     * somente ADMIN pode criar PROFESSOR.
     */
    @PatchMapping("/{id}/role")
    @Secured({"ROLE_ADMIN", "ROLE_PROFESSOR"})
    public ResponseEntity<UsuarioResponseDTO> promoverRole(
            @PathVariable Long id,
            @RequestParam UserRole novaRole) {
        UserRole roleAutorizado = getRoleAutenticado();
        UsuarioResponseDTO response = usuarioService.promoverRole(id, novaRole, roleAutorizado);
        return ResponseEntity.ok(response);
    }

    private UserRole getRoleAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDetailsService.findByEmail(email).getRole();
    }
}
