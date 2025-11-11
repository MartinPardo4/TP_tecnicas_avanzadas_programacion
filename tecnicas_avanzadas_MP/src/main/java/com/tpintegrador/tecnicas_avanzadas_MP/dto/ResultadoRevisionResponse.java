package com.tpintegrador.tecnicas_avanzadas_MP.dto;

import com.tpintegrador.tecnicas_avanzadas_MP.model.Calificacion;

import java.util.List;

public record ResultadoRevisionResponse(
        Long turnoId,
        List<Integer> puntajes,
        Integer puntajeTotal,
        Calificacion calificacion,
        String comentarioMecanico
) {
}


