package com.tpintegrador.tecnicas_avanzadas_MP.exception;

import org.springframework.http.HttpStatus;

public class AccesoDenegadoException extends ApiException {

    public AccesoDenegadoException(String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }
}

