package com.tpintegrador.tecnicas_avanzadas_MP.dto;

import java.time.LocalDateTime;

public record TurnoResponse(
        Long id,
        LocalDateTime fechaTurno,
        boolean confirmado,
        String observaciones,
        Long vehiculoId,
        String patenteVehiculo,
        Long mecanicoId
) {
}


