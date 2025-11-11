package com.tpintegrador.tecnicas_avanzadas_MP.service;

import com.tpintegrador.tecnicas_avanzadas_MP.dto.ResultadoRevisionResponse;
import com.tpintegrador.tecnicas_avanzadas_MP.dto.RevisionRequest;

public interface RevisionService {
    ResultadoRevisionResponse registrarRevision(Long turnoId, Long mecanicoId, RevisionRequest request);
    ResultadoRevisionResponse obtenerResultado(Long turnoId, Long usuarioId);
}

