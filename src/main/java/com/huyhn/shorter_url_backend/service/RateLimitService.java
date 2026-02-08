package com.huyhn.shorter_url_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final int LIMIT = 20;
    private static final int WINDOW_SECONDS = 60;

    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_SECONDS * 1000L;

        ZSetOperations<String, Object> set = redisTemplate.opsForZSet();

        set.removeRangeByScore(key, 0, windowStart);

        Long currentCount = set.zCard(key);

        if (currentCount != null && currentCount >= LIMIT) {
            return false;
        }

        set.add(key, now, now);

        redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));

        return true;
    }
}
