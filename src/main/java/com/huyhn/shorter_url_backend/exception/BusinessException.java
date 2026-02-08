package com.huyhn.shorter_url_backend.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode code;
    private final Map<String, String> args;

    public BusinessException(ErrorCode code) {
        super("");
        this.code = code;
        this.args = Map.of();
    }

    public BusinessException(ErrorCode code, Map<String, String> args) {
        super("");
        this.code = code;
        this.args = args;
    }
}
