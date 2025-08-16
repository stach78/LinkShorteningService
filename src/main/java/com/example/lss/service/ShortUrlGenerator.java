package com.example.lss.service;

import com.example.lss.repo.UrlMappingRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Set;

@Service
public class ShortUrlGenerator {
    private static final String VALID_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-";
    private static final Set<String> RESERVED = Set.of("api", "h2", "actuator");
    private static final int INITIAL_LEN = 4;
    private static final int MAX_LEN = 10;
    private static final int MAX_ATTEMPTS_PER_LEN = 5;

    private final SecureRandom random = new SecureRandom();
    private final UrlMappingRepository repo;

    public ShortUrlGenerator(UrlMappingRepository repo) {
        this.repo = repo;
    }

    public String next() {
        int len = INITIAL_LEN;
        while (len <= MAX_LEN) {
            for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_LEN; attempt++) {
                String candidate = randomString(len);
                if (isUsable(candidate)) return candidate;
            }
            len++;
        }
        throw new IllegalStateException("Unable to generate a unique short url after exhaustive attempts");
    }

    public void validateCustomShortUrl(String shortUrl) {
        if (shortUrl == null || shortUrl.isBlank())
            throw new IllegalArgumentException("Custom short url must not be blank");

        if (!shortUrl.matches("^[A-Za-z0-9_-]{3,30}$"))
            throw new IllegalArgumentException("Custom code must match ^[A-Za-z0-9_-]{3,30}$");

        String lower = shortUrl.toLowerCase(Locale.ROOT);
        if (RESERVED.contains(lower))
            throw new IllegalArgumentException("Custom shortUrl is reserved");

        if (repo.existsByShortUrl(shortUrl))
            throw new IllegalArgumentException("Custom shortUrl already taken");
    }

    private boolean isUsable(String shortUrl) {
        if (RESERVED.contains(shortUrl.toLowerCase(Locale.ROOT))) return false;
        return !repo.existsByShortUrl(shortUrl);
    }

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(VALID_CHARS.charAt(random.nextInt(VALID_CHARS.length())));
        }
        return sb.toString();
    }
}
