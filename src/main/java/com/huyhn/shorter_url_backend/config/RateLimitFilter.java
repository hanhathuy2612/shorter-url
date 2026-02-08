package com.huyhn.shorter_url_backend.config;

import com.huyhn.shorter_url_backend.service.IRateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final IRateLimitService rateLimitService;

    public RateLimitFilter(
            @Qualifier("luaScriptRateLimitService")
            IRateLimitService rateLimitService
    ) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        try {
            String requestURI = request.getRequestURI();
            String key = "rl:" + ip + ":" + requestURI;

            boolean allowed = rateLimitService.allowRequest(key);

            if (!allowed) {
                response.setStatus(429);
                response.getWriter().write("Too many requests");
                return;
            }
            log.info("Allowed request key: {}", key);

        } catch (Exception e) {
            log.error("Error while allowing request", e);
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
