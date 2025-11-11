package com.tpintegrador.tecnicas_avanzadas_MP.service.impl;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.LoginRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.LoginResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.RegistroUsuarioRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.UsuarioResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.NoAutenticadoException;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.RecursoNoEncontradoException;
import com.tpintegrador.tecnicas_avanzadas_MP.exception.SolicitudInvalidaException;
import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;
import com.tpintegrador.tecnicas_avanzadas_MP.repository.UsuarioRepository;
import com.tpintegrador.tecnicas_avanzadas_MP.security.JwtService;
import com.tpintegrador.tecnicas_avanzadas_MP.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UsuarioResponse registrar(RegistroUsuarioRequest request) {
        usuarioRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    throw new SolicitudInvalidaException("El email ya está registrado");
                });

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(request.rol())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearUsuario(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new NoAutenticadoException("Credenciales inválidas"));

        if (!usuario.isActivo() || !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new NoAutenticadoException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(token, usuario.getId(), usuario.getNombre(), usuario.getRol());
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private UsuarioResponse mapearUsuario(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}
