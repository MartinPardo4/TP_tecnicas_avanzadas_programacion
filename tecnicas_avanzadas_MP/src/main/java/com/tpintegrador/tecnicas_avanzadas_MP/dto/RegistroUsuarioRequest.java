package com.tpintegrador.tecnicas_avanzadas_MP.dto;

import com.tpintegrador.tecnicas_avanzadas_MP.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequest(
        @NotBlank String nombre,
        @Email @NotBlank String email,
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password,
        @NotNull Rol rol
) {
}


