package com.tpintegrador.tecnicas_avanzadas_MP.controller;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.ResultadoRevisionResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.RevisionRequest;
import com.tpintegrador.tecnicas_avanzadas_MP.security.UsuarioPrincipal;
import com.tpintegrador.tecnicas_avanzadas_MP.service.RevisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/turnos")
@RequiredArgsConstructor
public class RevisionController {

    private final RevisionService revisionService;

    @PostMapping("/{id}/revision")
    @PreAuthorize("hasRole('MECANICO')")
    public ResponseEntity<ResultadoRevisionResponse> registrarRevision(@PathVariable Long id,
                                                                       @RequestBody @Valid RevisionRequest request,
                                                                       Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        ResultadoRevisionResponse response = revisionService.registrarRevision(id, principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/resultado")
    @PreAuthorize("hasAnyRole('DUENO','MECANICO')")
    public ResponseEntity<ResultadoRevisionResponse> obtenerResultado(@PathVariable Long id,
                                                                      Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(revisionService.obtenerResultado(id, principal.getId()));
    }
}

