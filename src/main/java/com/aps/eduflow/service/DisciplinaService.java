package com.aps.eduflow.service;

import com.aps.eduflow.domain.dto.DisciplinaRequestDTO;
import com.aps.eduflow.domain.dto.DisciplinaResponseDTO;
import com.aps.eduflow.domain.entity.Disciplina;
import com.aps.eduflow.domain.exception.ConflitoException;
import com.aps.eduflow.domain.exception.RegraNegocioException;
import com.aps.eduflow.domain.repository.AtendimentoRepository;
import com.aps.eduflow.domain.repository.DisciplinaRepository;
import com.aps.eduflow.domain.repository.MonitoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final MonitoriaRepository monitoriaRepository;
    private final AtendimentoRepository atendimentoRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository,
                             MonitoriaRepository monitoriaRepository,
                             AtendimentoRepository atendimentoRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.monitoriaRepository = monitoriaRepository;
        this.atendimentoRepository = atendimentoRepository;
    }

    public DisciplinaResponseDTO criar(DisciplinaRequestDTO dto) {
        if (disciplinaRepository.existsByCodigo(dto.getCodigo().toUpperCase())) {
            throw new ConflitoException("Já existe uma disciplina com o código " + dto.getCodigo());
        }

        Disciplina disciplina = new Disciplina();
        disciplina.setNome(dto.getNome());
        disciplina.setCodigo(dto.getCodigo().toUpperCase());
        disciplina.setDescricao(dto.getDescricao());
        disciplina.setAtivo(true);

        disciplina = disciplinaRepository.save(disciplina);

        return new DisciplinaResponseDTO(disciplina);
    }

    public List<DisciplinaResponseDTO> listar(String busca) {
        List<Disciplina> disciplinas;

        if (busca != null && !busca.isBlank()) {
            if (busca.matches("^[A-Za-z]{3}.*")) {
                disciplinas = disciplinaRepository.findByCodigoContainingIgnoreCaseOrderByNomeAsc(busca);
            } else {
                disciplinas = disciplinaRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca);
            }
        } else {
            disciplinas = disciplinaRepository.findByAtivoTrueOrderByNomeAsc();
        }

        return disciplinas.stream()
                .map(DisciplinaResponseDTO::new)
                .toList();
    }

    public DisciplinaResponseDTO buscarPorId(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Disciplina não encontrada"));
        return new DisciplinaResponseDTO(disciplina);
    }

    public DisciplinaResponseDTO atualizar(Long id, DisciplinaRequestDTO dto) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Disciplina não encontrada"));

        if (!disciplina.getCodigo().equals(dto.getCodigo().toUpperCase())
                && disciplinaRepository.existsByCodigoAndIdNot(dto.getCodigo().toUpperCase(), id)) {
            throw new ConflitoException("Já existe uma disciplina com o código " + dto.getCodigo());
        }

        disciplina.setNome(dto.getNome());
        disciplina.setCodigo(dto.getCodigo().toUpperCase());
        disciplina.setDescricao(dto.getDescricao());

        disciplina = disciplinaRepository.save(disciplina);

        return new DisciplinaResponseDTO(disciplina);
    }

    public void deletar(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Disciplina não encontrada"));

        if (!disciplina.getAtivo()) {
            throw new RegraNegocioException("Disciplina já está inativa");
        }

        if (monitoriaRepository.existsByDisciplinaId(id)) {
            throw new RegraNegocioException("Não é possível excluir: existem monitores vinculados a esta disciplina");
        }

        if (atendimentoRepository.existsByDisciplinaId(id)) {
            throw new RegraNegocioException("Não é possível excluir: existem atendimentos registrados nesta disciplina");
        }

        disciplina.setAtivo(false);
        disciplinaRepository.save(disciplina);
    }
}
