package com.huyhn.shorter_url_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateShortUrlRequest {
    private String originalUrl;
}
