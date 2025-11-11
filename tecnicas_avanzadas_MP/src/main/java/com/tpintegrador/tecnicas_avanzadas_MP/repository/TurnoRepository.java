package com.tpintegrador.tecnicas_avanzadas_MP.repository;

import com.tpintegrador.tecnicas_avanzadas_MP.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByVehiculo_Dueno_Id(Long duenoId);
    List<Turno> findByMecanicoAsignado_Id(Long mecanicoId);
    Optional<Turno> findByIdAndVehiculo_Dueno_Id(Long id, Long duenoId);
}


