package com.huyhn.shorter_url_backend.service;

public interface IRateLimitService {
    int LIMIT = 20;
    int WINDOW_SECONDS = 60;

    boolean allowRequest(String key);
}
