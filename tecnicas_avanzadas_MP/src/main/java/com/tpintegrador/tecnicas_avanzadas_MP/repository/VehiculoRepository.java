package com.tpintegrador.tecnicas_avanzadas_MP.repository;

import com.tpintegrador.tecnicas_avanzadas_MP.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPatente(String patente);
    List<Vehiculo> findByDueno_Id(Long duenoId);
}
