package com.huyhn.shorter_url_backend.service;

import com.huyhn.shorter_url_backend.domain.ShortUrl;
import com.huyhn.shorter_url_backend.dto.CreateShortUrlRequest;
import com.huyhn.shorter_url_backend.dto.ShortUrlDTO;
import com.huyhn.shorter_url_backend.repository.ShortUrlRepository;
import com.huyhn.shorter_url_backend.utils.CodeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {
    private final ShortUrlRepository shortUrlRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final static String CLICK_KEY = "clicks::";
    private final static String SHORT_URL_VALUE = "short_url";
    private final static int FIXED_RATE = 15;

    @Value("${app.domain:yourDomain}")
    private String domain;

    @Cacheable(value = SHORT_URL_VALUE, key = "#code", sync = true)
    public String redirect(String code) {
        ShortUrl shortUrl = shortUrlRepository.findByCode(code)
                .orElseThrow(
                        () -> new RuntimeException("Code not found")
                );

        return shortUrl.getOriginalUrl();
    }

    public ShortUrlDTO getByCode(String code) {
        return shortUrlRepository
                .findByCode(code)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Code not found"));
    }

    public ShortUrlDTO create(CreateShortUrlRequest request) {
        try {
            URI uri = new URI(request.getOriginalUrl());
            String host = uri.getHost();

            if (host.equals("localhost") || host.equals(domain)) {
                throw new RuntimeException("Self redirect is not allowed");
            }

            ShortUrl shortUrl = new ShortUrl();
            shortUrl.setOriginalUrl(request.getOriginalUrl());
            shortUrl = shortUrlRepository.save(shortUrl);

            shortUrl.setCode(CodeUtils.encode(shortUrl.getId()));
            shortUrl = shortUrlRepository.save(shortUrl);

            return ShortUrlDTO.builder()
                    .id(shortUrl.getId())
                    .originalUrl(shortUrl.getOriginalUrl())
                    .code(shortUrl.getCode())
                    .clicks(0L)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @CacheEvict(value = SHORT_URL_VALUE, key = "#code")
    public void delete(String code) {
        ShortUrl shortUrl = shortUrlRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Code not found"));
        shortUrlRepository.delete(shortUrl);
    }

    public List<ShortUrlDTO> getPaginated(Pageable pageable) {
        return shortUrlRepository.findAll(pageable).map(this::toDTO).toList();
    }

    private ShortUrlDTO toDTO(ShortUrl shortUrl) {
        return ShortUrlDTO.builder()
                .id(shortUrl.getId())
                .originalUrl(shortUrl.getOriginalUrl())
                .code(shortUrl.getCode())
                .clicks(shortUrl.getClicks())
                .build();
    }

    public void increaseClick(String code) {
        redisTemplate.opsForValue().increment(CLICK_KEY + code);
    }

    @Scheduled(fixedRate = FIXED_RATE, timeUnit = TimeUnit.SECONDS)
    public void syncClicksToDB() {
        Set<String> keys = redisTemplate.keys(CLICK_KEY + "*");

        for (String key : keys) {
            String code = key.replace(CLICK_KEY, "");
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            if (count == null) {
                continue;
            }

            shortUrlRepository.increaseClicks(code, Long.valueOf(count));

            redisTemplate.delete(key);
        }
    }
}
