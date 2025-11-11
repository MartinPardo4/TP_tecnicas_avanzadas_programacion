package com.tpintegrador.tecnicas_avanzadas_MP.service.impl;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.VehiculoRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.VehiculoResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.AccesoDenegadoException;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.RecursoNoEncontradoException;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.SolicitudInvalidaException;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Rol;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Vehiculo;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.UsuarioRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.VehiculoRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public VehiculoResponse registrarVehiculo(Long duenoId, VehiculoRequest request) {
        Usuario dueno = obtenerDueno(duenoId);

        vehiculoRepository.findByPatente(request.patente())
                .ifPresent(v -> {
                    throw new SolicitudInvalidaException("La patente ya está registrada");
                });

        Vehiculo vehiculo = Vehiculo.builder()
                .patente(request.patente().toUpperCase())
                .marca(request.marca())
                .modelo(request.modelo())
                .anio(request.anio())
                .kilometraje(request.kilometraje())
                .dueno(dueno)
                .build();

        Vehiculo guardado = vehiculoRepository.save(vehiculo);
        return mapearVehiculo(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarPorDueno(Long duenoId) {
        obtenerDueno(duenoId);
        return vehiculoRepository.findByDueno_Id(duenoId)
                .stream()
                .map(this::mapearVehiculo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehiculo obtenerPorId(Long vehiculoId) {
        return vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));
    }

    private Usuario obtenerDueno(Long duenoId) {
        Usuario usuario = usuarioRepository.findById(duenoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dueño no encontrado"));
        if (usuario.getRol() != Rol.DUENO) {
            throw new AccesoDenegadoException("El usuario no tiene rol de dueño");
        }
        if (!usuario.isActivo()) {
            throw new AccesoDenegadoException("El usuario no está activo");
        }
        return usuario;
    }

    private VehiculoResponse mapearVehiculo(Vehiculo vehiculo) {
        return new VehiculoResponse(
                vehiculo.getId(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getKilometraje()
        );
    }
}
