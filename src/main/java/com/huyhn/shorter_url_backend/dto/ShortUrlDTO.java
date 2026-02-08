package com.huyhn.shorter_url_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlDTO implements Serializable {

    private Long id;

    private String code;

    private String originalUrl;

    private Long clicks;
}
