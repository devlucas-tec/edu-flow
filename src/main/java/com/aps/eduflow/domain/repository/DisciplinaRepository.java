package com.aps.eduflow.domain.repository;

import com.aps.eduflow.domain.entity.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    Optional<Disciplina> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    List<Disciplina> findByAtivoTrueOrderByNomeAsc();

    List<Disciplina> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Disciplina> findByCodigoContainingIgnoreCaseOrderByNomeAsc(String codigo);
}
