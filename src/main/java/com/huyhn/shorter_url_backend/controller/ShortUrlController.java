package com.huyhn.shorter_url_backend.controller;

import com.huyhn.shorter_url_backend.dto.CreateShortUrlRequest;
import com.huyhn.shorter_url_backend.dto.ShortUrlDTO;
import com.huyhn.shorter_url_backend.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-urls")
public class ShortUrlController {
    private final ShortUrlService shortUrlService;

    @GetMapping
    public ResponseEntity<List<ShortUrlDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(shortUrlService.getPaginated(pageable));
    }

    @PostMapping
    public ResponseEntity<ShortUrlDTO> create(@RequestBody @Validated CreateShortUrlRequest request) {
        ShortUrlDTO shortUrl = shortUrlService.create(request);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("{code}/redirect")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originUrl = shortUrlService.redirect(code);
        shortUrlService.increaseClick(code);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originUrl))
                .build();
    }

    @GetMapping("{code}")
    public ResponseEntity<ShortUrlDTO> getByCode(@PathVariable String code) {
        ShortUrlDTO shortUrl = shortUrlService.getByCode(code);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(shortUrl);
    }

    @DeleteMapping("{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        shortUrlService.delete(code);
        return ResponseEntity.noContent().build();
    }
}
