package com.example.report.service;

import com.example.report.dto.ReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.Duration;
import java.util.Optional;

/**
 * Thin wrapper around RedisTemplate for caching report status/results by a
 * deterministic cache key derived from the request payload. This lets identical
 * requests (same type + params + requester) short-circuit straight to a cached
 * result instead of re-triggering generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private static final String STATUS_KEY_PREFIX = "report:status:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${report.cache.ttl-minutes:30}")
    private long ttlMinutes;

    public String buildCacheKey(String reportType, String requestedBy, String parametersJson) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = reportType + "|" + requestedBy + "|" + parametersJson;
            byte[] hash = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // Fallback: not cryptographically ideal but keeps the service functional.
            return Integer.toHexString((reportType + requestedBy + parametersJson).hashCode());
        }
    }

    public Optional<ReportResponse> getStatus(String cacheKey) {
        try {
            Object value = redisTemplate.opsForValue().get(STATUS_KEY_PREFIX + cacheKey);
            if (value instanceof ReportResponse response) {
                return Optional.of(response);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Redis GET failed for key={}, falling back to DB", cacheKey, e);
            return Optional.empty();
        }
    }

    public void putStatus(String cacheKey, ReportResponse response) {
        try {
            redisTemplate.opsForValue().set(STATUS_KEY_PREFIX + cacheKey, response, Duration.ofMinutes(ttlMinutes));
        } catch (Exception e) {
            log.warn("Redis SET failed for key={}, continuing without cache", cacheKey, e);
        }
    }

    public void evictStatus(String cacheKey) {
        try {
            redisTemplate.delete(STATUS_KEY_PREFIX + cacheKey);
        } catch (Exception e) {
            log.warn("Redis DELETE failed for key={}", cacheKey, e);
        }
    }
}
