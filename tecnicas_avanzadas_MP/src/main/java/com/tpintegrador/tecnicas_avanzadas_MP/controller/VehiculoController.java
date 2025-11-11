package com.tpintegrador.tecnicas_avanzadas_MP.controller;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.VehiculoRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.VehiculoResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.security.UsuarioPrincipal;
import com.tpintegrador.tecnicas_avanzadas_MP.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<VehiculoResponse> registrarVehiculo(@Valid @RequestBody VehiculoRequest request,
                                                               Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        VehiculoResponse response = vehiculoService.registrarVehiculo(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<VehiculoResponse>> listarVehiculos(Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(vehiculoService.listarPorDueno(principal.getId()));
    }
}


