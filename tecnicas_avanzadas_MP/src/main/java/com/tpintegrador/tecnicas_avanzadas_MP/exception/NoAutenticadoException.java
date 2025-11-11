package com.tpintegrador.tecnicas_avanzadas_MP.exception;

import org.springframework.http.HttpStatus;

public class NoAutenticadoException extends ApiException {

    public NoAutenticadoException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}

