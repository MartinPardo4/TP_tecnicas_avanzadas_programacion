package com.tpintegrador.tecnicas_avanzadas_MP.dto;

import com.tpintegrador.tecnicas_avanzadas_MP.model.Rol;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        Rol rol,
        boolean activo
) {
}


