package com.tpintegrador.tecnicas_avanzadas_MP.service.impl;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.ResultadoRevisionResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.RevisionRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.AccesoDenegadoException;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.RecursoNoEncontradoException;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.SolicitudInvalidaException;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Calificacion;
import com.tpintegrador.tecnicas_avanzadas_MP.model.ResultadoRevision;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Turno;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.ResultadoRevisionRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.service.RevisionService;
import com.tpintegrador.tecnicas_avanzadas_MP.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevisionServiceImpl implements RevisionService {

    private final ResultadoRevisionRepository resultadoRevisionRepository;
    private final TurnoService turnoService;

    @Override
    @Transactional
    public ResultadoRevisionResponse registrarRevision(Long turnoId, Long mecanicoId, RevisionRequest request) {
        Turno turno = turnoService.obtenerPorId(turnoId);

        if (!turno.isConfirmado()) {
            throw new SolicitudInvalidaException("El turno aún no está confirmado");
        }

        if (turno.getMecanicoAsignado() == null || !turno.getMecanicoAsignado().getId().equals(mecanicoId)) {
            throw new AccesoDenegadoException("El turno no está asignado al mecánico autenticado");
        }

        if (turno.getResultado() != null) {
            throw new SolicitudInvalidaException("El turno ya tiene un resultado registrado");
        }

        List<Integer> puntajes = request.puntajes();
        if (puntajes.stream().anyMatch(p -> p < 1 || p > 10)) {
            throw new SolicitudInvalidaException("Los puntajes deben estar entre 1 y 10");
        }

        int total = puntajes.stream().mapToInt(Integer::intValue).sum();
        boolean algunInferiorA5 = puntajes.stream().anyMatch(p -> p < 5);

        Calificacion calificacion;
        if (total < 40 || algunInferiorA5) {
            calificacion = Calificacion.RECHEQUEAR;
        } else {
            calificacion = Calificacion.SEGURO;
        }

        ResultadoRevision resultado = ResultadoRevision.builder()
                .puntajes(puntajes)
                .puntajeTotal(total)
                .calificacion(calificacion)
                .comentarioMecanico(request.comentarioMecanico())
                .turno(turno)
                .build();

        ResultadoRevision guardado = resultadoRevisionRepository.save(resultado);
        turno.setResultado(guardado);

        return mapearResultado(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoRevisionResponse obtenerResultado(Long turnoId, Long usuarioId) {
        Turno turno = turnoService.obtenerPorId(turnoId);

        boolean esDueno = turno.getVehiculo().getDueno().getId().equals(usuarioId);
        boolean esMecanico = turno.getMecanicoAsignado() != null
                && turno.getMecanicoAsignado().getId().equals(usuarioId);

        if (!esDueno && !esMecanico) {
            throw new AccesoDenegadoException("El usuario no tiene acceso a este resultado");
        }

        ResultadoRevision resultado = resultadoRevisionRepository.findByTurno_Id(turnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Resultado no encontrado"));
        return mapearResultado(resultado);
    }

    private ResultadoRevisionResponse mapearResultado(ResultadoRevision resultado) {
        return new ResultadoRevisionResponse(
                resultado.getTurno().getId(),
                List.copyOf(resultado.getPuntajes()),
                resultado.getPuntajeTotal(),
                resultado.getCalificacion(),
                resultado.getComentarioMecanico()
        );
    }
}

