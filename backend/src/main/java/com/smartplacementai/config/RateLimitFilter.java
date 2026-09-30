package com.smartplacementai.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-IP rate limiter for auth + AI endpoints.
 * NOT a @Component on purpose: it is added inside the Spring Security chain (see SecurityConfig)
 * so that CORS headers are already on the response and the browser can actually read the 429.
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> LIMITED_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/ats/analyze",
            "/api/v1/jd/analyze",
            "/api/v1/roadmap/generate"
    );

    private static final int MAX_TRACKED_KEYS = 10_000;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int perMinute;

    public RateLimitFilter(int perMinute) {
        this.perMinute = perMinute;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || !LIMITED_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (buckets.size() > MAX_TRACKED_KEYS) {
            buckets.clear(); // crude memory bound; fine for a single-node app
        }

        String key = request.getRemoteAddr() + ":" + path;
        Bucket bucket = buckets.computeIfAbsent(key, k -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(perMinute)
                        .refillIntervally(perMinute, Duration.ofMinutes(1))
                        .build())
                .build());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
            return;
        }

        // Write the response directly. sendError() would trigger an ERROR dispatch that
        // Spring Security then blocks (turning the 429 into 401/403).
        response.setStatus(429);
        response.setHeader("Retry-After", "60");
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"status\":429,\"error\":\"Too Many Requests\","
                        + "\"message\":\"Too many requests - please slow down.\"}");
    }
}
