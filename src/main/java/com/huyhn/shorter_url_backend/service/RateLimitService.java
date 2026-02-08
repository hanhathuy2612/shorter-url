package com.huyhn.shorter_url_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final static long CAPACITY = 10;
    private final static long REFILL_SECONDS = 30;

    public boolean allowRequest(String key) {
        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(REFILL_SECONDS));
            }

            return count <= CAPACITY;
        } catch (Exception e) {
            log.error(e.getMessage());
            redisTemplate.delete(key);
            return true;
        }
    }
}
