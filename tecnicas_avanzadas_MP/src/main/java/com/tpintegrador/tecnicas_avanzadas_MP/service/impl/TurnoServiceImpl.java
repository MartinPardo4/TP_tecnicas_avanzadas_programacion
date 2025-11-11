package com.tpintegrador.tecnicas_avanzadas_MP.service.impl;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.TurnoRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.TurnoResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.AccesoDenegadoException;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.RecursoNoEncontradoException;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.SolicitudInvalidaException;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Rol;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Turno;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Vehiculo;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.TurnoRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.UsuarioRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.service.TurnoService;
import com.tpintegrador.tecnicas_avanzadas_MP.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final VehiculoService vehiculoService;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public TurnoResponse solicitarTurno(Long duenoId, TurnoRequest request) {
        Vehiculo vehiculo = vehiculoService.obtenerPorId(request.vehiculoId());
        if (!vehiculo.getDueno().getId().equals(duenoId)) {
            throw new AccesoDenegadoException("El vehículo no pertenece al dueño autenticado");
        }

        Turno turno = Turno.builder()
                .vehiculo(vehiculo)
                .fechaTurno(request.fechaTurno())
                .observaciones(request.observaciones())
                .confirmado(false)
                .build();

        Turno guardado = turnoRepository.save(turno);
        return mapearTurno(guardado);
    }

    @Override
    @Transactional
    public TurnoResponse confirmarTurno(Long turnoId, Long mecanicoId) {
        Turno turno = obtenerPorId(turnoId);
        if (turno.isConfirmado()) {
            throw new SolicitudInvalidaException("El turno ya fue confirmado");
        }

        Usuario mecanico = usuarioRepository.findById(mecanicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mecánico no encontrado"));

        if (mecanico.getRol() != Rol.MECANICO) {
            throw new AccesoDenegadoException("El usuario no tiene rol de mecánico");
        }

        turno.setConfirmado(true);
        turno.setMecanicoAsignado(mecanico);
        Turno actualizado = turnoRepository.save(turno);
        return mapearTurno(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponse> listarTurnosDueno(Long duenoId) {
        return turnoRepository.findByVehiculo_Dueno_Id(duenoId)
                .stream()
                .map(this::mapearTurno)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponse> listarTurnosMecanico(Long mecanicoId) {
        return turnoRepository.findByMecanicoAsignado_Id(mecanicoId)
                .stream()
                .map(this::mapearTurno)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Turno obtenerPorId(Long turnoId) {
        return turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado"));
    }

    private TurnoResponse mapearTurno(Turno turno) {
        return new TurnoResponse(
                turno.getId(),
                turno.getFechaTurno(),
                turno.isConfirmado(),
                turno.getObservaciones(),
                turno.getVehiculo().getId(),
                turno.getVehiculo().getPatente(),
                turno.getMecanicoAsignado() != null ? turno.getMecanicoAsignado().getId() : null
        );
    }
}

