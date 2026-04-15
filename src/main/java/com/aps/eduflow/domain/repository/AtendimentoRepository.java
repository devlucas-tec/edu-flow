package com.aps.eduflow.domain.repository;

import com.aps.eduflow.domain.entity.Atendimento;
import com.aps.eduflow.domain.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByAlunoId(Long alunoId);

    List<Atendimento> findByMonitorId(Long monitorId);

    List<Atendimento> findByDisciplinaId(Long disciplinaId);

    List<Atendimento> findByStatus(SessionStatus status);

    List<Atendimento> findByMonitorIdAndStatus(Long monitorId, SessionStatus status);

    List<Atendimento> findByMonitorIdAndDataHoraBetween(Long monitorId, LocalDateTime inicio, LocalDateTime fim);

    boolean existsByDisciplinaId(Long disciplinaId);
}
