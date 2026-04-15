package com.aps.eduflow.domain.repository;

import com.aps.eduflow.domain.entity.Monitoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitoriaRepository extends JpaRepository<Monitoria, Long> {

    List<Monitoria> findByMonitorId(Long monitorId);

    List<Monitoria> findByDisciplinaId(Long disciplinaId);

    boolean existsByMonitorIdAndDisciplinaId(Long monitorId, Long disciplinaId);

    boolean existsByDisciplinaId(Long disciplinaId);
}
