package com.tpintegrador.tecnicas_avanzadas_MP.dto;

public record VehiculoResponse(
        Long id,
        String patente,
        String marca,
        String modelo,
        Integer anio,
        Integer kilometraje
) {
}


