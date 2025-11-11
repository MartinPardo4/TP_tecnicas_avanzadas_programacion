package com.tpintegrador.tecnicas_avanzadas_MP.dto;

import com.tpintegrador.tecnicas_avanzadas_MP.model.Rol;

public record LoginResponse(
        String token,
        Long userId,
        String nombre,
        Rol rol
) {
}


