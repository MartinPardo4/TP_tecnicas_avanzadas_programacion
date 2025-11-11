package com.tpintegrador.tecnicas_avanzadas_MP.repository;

import com.tpintegrador.tecnicas_avanzadas_MP.model.ResultadoRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultadoRevisionRepository extends JpaRepository<ResultadoRevision, Long> {
    Optional<ResultadoRevision> findByTurno_Id(Long turnoId);
}


