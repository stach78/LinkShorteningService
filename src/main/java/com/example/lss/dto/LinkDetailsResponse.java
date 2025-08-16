package com.example.lss.dto;

import java.time.Instant;

public record LinkDetailsResponse(
        String shortUrl,
        String originalUrl,
        long clickCount,
        Instant createdAt,
        Instant lastAccessed
) {}
