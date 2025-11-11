package com.tpintegrador.tecnicas_avanzadas_MP.service;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.LoginRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.LoginResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.RegistroUsuarioRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.UsuarioResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;

public interface UsuarioService {
    UsuarioResponse registrar(RegistroUsuarioRequest request);
    LoginResponse login(LoginRequest request);
    Usuario obtenerPorId(Long id);
}
