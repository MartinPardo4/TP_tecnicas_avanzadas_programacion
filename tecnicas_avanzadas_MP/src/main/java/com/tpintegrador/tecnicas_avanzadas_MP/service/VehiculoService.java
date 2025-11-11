package com.tpintegrador.tecnicas_avanzadas_MP.service;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.VehiculoRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.VehiculoResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Vehiculo;

import java.util.List;

public interface VehiculoService {
    VehiculoResponse registrarVehiculo(Long duenoId, VehiculoRequest request);
    List<VehiculoResponse> listarPorDueno(Long duenoId);
    Vehiculo obtenerPorId(Long vehiculoId);
}
