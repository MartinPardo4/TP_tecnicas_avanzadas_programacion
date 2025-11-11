package com.tpintegrador.tecnicas_avanzadas_MP.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class ApiException extends ResponseStatusException {

    protected ApiException(HttpStatus status, String reason) {
        super(status, reason);
    }
}

