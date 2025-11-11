package com.tpintegrador.tecnicas_avanzadas_MP.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RevisionRequest(
        @NotNull @Size(min = 8, max = 8, message = "Se deben enviar exactamente 8 puntajes") List<@NotNull Integer> puntajes,
        String comentarioMecanico
) {
}


