package com.tpintegrador.tecnicas_avanzadas_MP.exception;

import java.time.OffsetDateTime;
import java.util.List;

public class ApiError {

    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final OffsetDateTime timestamp;
    private final List<String> errors;

    public ApiError(int status, String error, String message, String path, OffsetDateTime timestamp, List<String> errors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }
}

