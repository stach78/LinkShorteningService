package com.example.lss.service;

import com.example.lss.dto.ShortenRequest;
import com.example.lss.dto.ShortenResponse;
import com.example.lss.entity.UrlMapping;
import com.example.lss.repo.UrlMappingRepository;
import com.example.lss.util.InputUrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkShorteningServiceTest {

    private static final String BASE_URL = "http://localhost";

    @Mock UrlMappingRepository repo;
    @Mock ShortUrlGenerator shortUrlGenerator;
    @Mock InputUrlValidator urlValidator;

    LinkShorteningService service;

    @BeforeEach
    void setUp() {
        service = new LinkShorteningService(repo, shortUrlGenerator, urlValidator, BASE_URL);
    }

    @Test
    void createShortLink_generated_success() {
        ShortenRequest req = new ShortenRequest();
        req.setUrl("https://Example.com/Page");
        when(urlValidator.validate("https://Example.com/Page"))
                .thenReturn("https://example.com/Page");
        when(shortUrlGenerator.next()).thenReturn("abc123");

        ArgumentCaptor<UrlMapping> saved = ArgumentCaptor.forClass(UrlMapping.class);
        when(repo.save(saved.capture())).thenAnswer(inv -> inv.getArgument(0));

        // when
        ShortenResponse resp = service.createShortLink(req);

        // then
        assertEquals(BASE_URL + "/abc123", resp.getShortenedUrl());
        UrlMapping entity = saved.getValue();
        assertEquals("https://example.com/Page", entity.getOriginalUrl());
        assertEquals("abc123", entity.getShortUrl());
        assertFalse(entity.isCustom());
        verify(shortUrlGenerator, times(1)).next();
        verify(shortUrlGenerator, never()).validateCustomShortUrl(anyString());
    }

    @Test
    void createShortLink_custom_success() {
        // given
        ShortenRequest req = new ShortenRequest();
        req.setUrl("https://example.com");
        req.setCustomShortUrl("my-code");
        when(urlValidator.validate("https://example.com")).thenReturn("https://example.com");

        ArgumentCaptor<String> customCap = ArgumentCaptor.forClass(String.class);
        doNothing().when(shortUrlGenerator).validateCustomShortUrl(customCap.capture());
        when(repo.save(any(UrlMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ShortenResponse resp = service.createShortLink(req);

        // then
        assertEquals("my-code", customCap.getValue());
        assertEquals(BASE_URL + "/my-code", resp.getShortenedUrl());

        ArgumentCaptor<UrlMapping> saved = ArgumentCaptor.forClass(UrlMapping.class);
        verify(repo).save(saved.capture());
        UrlMapping e = saved.getValue();
        assertTrue(e.isCustom());
        assertEquals("my-code", e.getShortUrl());
        assertEquals("https://example.com", e.getOriginalUrl());
        verify(shortUrlGenerator, never()).next();
    }

    @Test
    void createShortLink_custom_conflict_throwsIllegalArgument() {
        // given
        ShortenRequest req = new ShortenRequest();
        req.setUrl("https://example.com");
        req.setCustomShortUrl("taken");
        when(urlValidator.validate("https://example.com")).thenReturn("https://example.com");
        doNothing().when(shortUrlGenerator).validateCustomShortUrl("taken");

        when(repo.save(any(UrlMapping.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        // when / then
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.createShortLink(req));
        assertTrue(ex.getMessage().toLowerCase().contains("custom"));
        verify(shortUrlGenerator, never()).next();
    }

    @Test
    void createShortLink_generated_conflict_retriesOnce() {
        // given
        ShortenRequest req = new ShortenRequest();
        req.setUrl("https://example.com");
        when(urlValidator.validate("https://example.com")).thenReturn("https://example.com");

        when(shortUrlGenerator.next()).thenReturn("dup123", "ok456");

        when(repo.save(any(UrlMapping.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        ShortenResponse resp = service.createShortLink(req);

        // then
        assertEquals(BASE_URL + "/ok456", resp.getShortenedUrl());
        verify(shortUrlGenerator, times(2)).next();

        ArgumentCaptor<UrlMapping> cap = ArgumentCaptor.forClass(UrlMapping.class);
        verify(repo, times(2)).save(cap.capture());

        UrlMapping last = cap.getAllValues().get(1);
        assertEquals("ok456", last.getShortUrl());
    }

    @Test
    void createShortLink_blankCustomFallsBackToGenerated() {
        // given
        ShortenRequest req = new ShortenRequest();
        req.setUrl("https://example.com");
        req.setCustomShortUrl("   "); // blank → should be treated as not custom
        when(urlValidator.validate("https://example.com")).thenReturn("https://example.com");
        when(shortUrlGenerator.next()).thenReturn("abc999");
        when(repo.save(any(UrlMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ShortenResponse resp = service.createShortLink(req);

        // then
        assertEquals(BASE_URL + "/abc999", resp.getShortenedUrl());
        verify(shortUrlGenerator).next();
        verify(shortUrlGenerator, never()).validateCustomShortUrl(any());
    }

    @Test
    void resolve_found_incrementsCounterAndReturnsMapping() {
        // given
        UrlMapping m = new UrlMapping();
        m.setId(2L);
        m.setShortUrl("abc");
        m.setOriginalUrl("https://example.com");
        when(repo.findByShortUrl("abc")).thenReturn(Optional.of(m));
        when(repo.incrementClickCount(eq(2L), any(Instant.class))).thenReturn(1);

        // when
        UrlMapping out = service.resolve("abc");

        // then
        assertSame(m, out);
        verify(repo).incrementClickCount(eq(2L), any(Instant.class));
    }

    @Test
    void resolve_notFound_throwsDomainException() {
        when(repo.findByShortUrl("xyz")).thenReturn(Optional.empty());
        assertThrows(LinkShorteningService.NotFoundException.class, () -> service.resolve("xyz"));
        verify(repo, never()).incrementClickCount(anyLong(), any());
    }
}
