package com.huyhn.shorter_url_backend.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class LuaScriptRateLimitService implements IRateLimitService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Long> script;

    public LuaScriptRateLimitService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.script = new DefaultRedisScript<>();
        this.script.setResultType(Long.class);
        this.script.setScriptText("""
                    local key = KEYS[1]
                    local now = tonumber(ARGV[1])
                    local window = tonumber(ARGV[2])
                    local limit = tonumber(ARGV[3])
                
                    local window_start = now - window
                
                    redis.call('ZREMRANGEBYSCORE', key, 0, window_start)
                    local current = redis.call('ZCARD', key)
                
                    if current >= limit then
                        return 0
                    end
                
                    redis.call('ZADD', key, now, now)
                    redis.call('EXPIRE', key, math.floor(window / 1000))
                
                    return 1
                """);
    }

    @Override
    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                now,
                WINDOW_SECONDS * 1000,
                LIMIT
        );

        return result == 1;
    }
}
