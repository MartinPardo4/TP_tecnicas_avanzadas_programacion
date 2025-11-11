package com.tpintegrador.tecnicas_avanzadas_MP.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TurnoRequest(
        @NotNull Long vehiculoId,
        @NotNull @Future LocalDateTime fechaTurno,
        @Size(max = 1000) String observaciones
) {
}


