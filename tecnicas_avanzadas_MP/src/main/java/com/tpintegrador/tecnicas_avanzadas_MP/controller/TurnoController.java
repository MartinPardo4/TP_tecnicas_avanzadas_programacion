package com.tpintegrador.tecnicas_avanzadas_MP.controller;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.TurnoRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.TurnoResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.security.UsuarioPrincipal;
import com.tpintegrador.tecnicas_avanzadas_MP.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @PostMapping
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<TurnoResponse> solicitarTurno(@Valid @RequestBody TurnoRequest request,
                                                        Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(turnoService.solicitarTurno(principal.getId(), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('DUENO')")
    public ResponseEntity<List<TurnoResponse>> listarTurnosDueno(Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(turnoService.listarTurnosDueno(principal.getId()));
    }

    @GetMapping("/asignados")
    @PreAuthorize("hasRole('MECANICO')")
    public ResponseEntity<List<TurnoResponse>> listarTurnosMecanico(Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(turnoService.listarTurnosMecanico(principal.getId()));
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('MECANICO')")
    public ResponseEntity<TurnoResponse> confirmarTurno(@PathVariable Long id, Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(turnoService.confirmarTurno(id, principal.getId()));
    }
}

