package com.tpintegrador.tecnicas_avanzadas_MP.controller;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.LoginRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.LoginResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.RegistroUsuarioRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.UsuarioResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        UsuarioResponse response = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }
}


