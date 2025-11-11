package com.tpintegrador.tecnicas_avanzadas_MP.exception;

import org.springframework.http.HttpStatus;

public class SolicitudInvalidaException extends ApiException {

    public SolicitudInvalidaException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}

