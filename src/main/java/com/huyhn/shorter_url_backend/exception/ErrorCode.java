package com.huyhn.shorter_url_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    ENTITY_CONFLICT(HttpStatus.CONFLICT, "error.entity.conflict", "This entity is already exists"),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "error.entity.not.found", "Entity not found"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "error.bad.request", "Bad request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.internal.server.error", "Internal server error"),

    SELF_HOST_NOT_ALLOW(HttpStatus.BAD_REQUEST, "error.self.host.not.allow", "Self redirect is not allowed"),
    HOST_EMPTY(HttpStatus.BAD_REQUEST, "error.host.empty", "Host is empty"),
    ORIGINAL_URL_EMPTY(HttpStatus.BAD_REQUEST, "error.url.empty", "Original url is empty"),
    INVALID_URL(HttpStatus.BAD_REQUEST, "error.invalid.url", "Invalid URL: {{url}}"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
