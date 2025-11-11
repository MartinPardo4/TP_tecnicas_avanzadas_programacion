package com.tpintegrador.tecnicas_avanzadas_MP.service;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.TurnoRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.TurnoResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Turno;

import java.util.List;

public interface TurnoService {
    TurnoResponse solicitarTurno(Long duenoId, TurnoRequest request);
    TurnoResponse confirmarTurno(Long turnoId, Long mecanicoId);
    List<TurnoResponse> listarTurnosDueno(Long duenoId);
    List<TurnoResponse> listarTurnosMecanico(Long mecanicoId);
    Turno obtenerPorId(Long turnoId);
}


