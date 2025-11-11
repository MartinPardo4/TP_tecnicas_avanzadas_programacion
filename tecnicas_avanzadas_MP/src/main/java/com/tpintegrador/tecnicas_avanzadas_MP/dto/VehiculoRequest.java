package com.tpintegrador.tecnicas_avanzadas_MP.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehiculoRequest(
        @NotBlank String patente,
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotNull @Min(1900) Integer anio,
        @NotNull @Min(0) Integer kilometraje
) {
}


