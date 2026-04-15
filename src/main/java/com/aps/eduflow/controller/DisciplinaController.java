package com.aps.eduflow.controller;

import com.aps.eduflow.domain.dto.DisciplinaRequestDTO;
import com.aps.eduflow.domain.dto.DisciplinaResponseDTO;
import com.aps.eduflow.service.DisciplinaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    /**
     * Criar nova disciplina — requer ADMIN ou PROFESSOR.
     */
    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_PROFESSOR"})
    public ResponseEntity<DisciplinaResponseDTO> criar(@RequestBody @Valid DisciplinaRequestDTO dto) {
        DisciplinaResponseDTO response = disciplinaService.criar(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/disciplinas/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Listar todas as disciplinas — aberto para autenticados, ordenado alfabeticamente.
     * Suporta filtro por busca (?busca=Algoritmos ou ?busca=ADS).
     */
    @GetMapping
    public ResponseEntity<List<DisciplinaResponseDTO>> listar(
            @RequestParam(required = false) String busca) {
        List<DisciplinaResponseDTO> response = disciplinaService.listar(busca);
        return ResponseEntity.ok(response);
    }

    /**
     * Detalhes de uma disciplina específica — aberto para autenticados.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DisciplinaResponseDTO> buscarPorId(@PathVariable Long id) {
        DisciplinaResponseDTO response = disciplinaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar disciplina — requer ADMIN ou PROFESSOR.
     */
    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_PROFESSOR"})
    public ResponseEntity<DisciplinaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DisciplinaRequestDTO dto) {
        DisciplinaResponseDTO response = disciplinaService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Excluir disciplina (soft delete) — requer ADMIN.
     * Valida se não há monitores ou atendimentos vinculados.
     */
    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        disciplinaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
