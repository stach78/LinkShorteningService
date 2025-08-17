package com.example.lss.service;

import com.example.lss.repo.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "app.shortUrl.initial-len=4",
        "app.shortUrl.max-len=10",
        "app.shortUrl.attempts-per-len=5"
})
class ShortUrlGeneratorTest {

    @MockitoBean
    UrlMappingRepository repo;
    @Autowired
    ShortUrlGenerator gen;

    @Value("${app.shortUrl.initial-len}")
    int minLen;

    @Value("${app.shortUrl.max-len}")
    int maxLen;

    @Test
    void nextGeneratesUsable() {
        Mockito.when(repo.existsByShortUrl(Mockito.anyString())).thenReturn(false);

        String shortUrl = gen.next();

        assertNotNull(shortUrl);
        assertTrue(shortUrl.matches("^[A-Za-z0-9_-]{" + minLen + "," + maxLen + "}$"));
    }

    @Test
    void customValidationRejectsReservedTooShortAndTaken() {
        Mockito.when(repo.existsByShortUrl("taken")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> gen.validateCustomShortUrl(null));
        assertThrows(IllegalArgumentException.class, () -> gen.validateCustomShortUrl("a"));
        assertThrows(IllegalArgumentException.class, () -> gen.validateCustomShortUrl("api"));
        assertThrows(IllegalArgumentException.class, () -> gen.validateCustomShortUrl("taken"));
    }
}
