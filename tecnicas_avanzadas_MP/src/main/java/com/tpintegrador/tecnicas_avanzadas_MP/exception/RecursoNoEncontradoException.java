package com.tpintegrador.tecnicas_avanzadas_MP.exception;

import org.springframework.http.HttpStatus;

public class RecursoNoEncontradoException extends ApiException {

    public RecursoNoEncontradoException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
}

