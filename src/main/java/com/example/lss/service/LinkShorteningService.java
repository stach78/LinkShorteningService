package com.example.lss.service;

import com.example.lss.dto.ShortenRequest;
import com.example.lss.dto.ShortenResponse;
import com.example.lss.entity.UrlMapping;
import com.example.lss.repo.UrlMappingRepository;
import com.example.lss.util.InputUrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class LinkShorteningService {

    private final UrlMappingRepository repo;
    private final ShortUrlGenerator shortUrlGenerator;
    private final InputUrlValidator urlValidator;
    private final String baseUrl;

    public LinkShorteningService(UrlMappingRepository repo,
                                 ShortUrlGenerator shortUrlGenerator,
                                 InputUrlValidator urlValidator,
                                 @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.repo = repo;
        this.shortUrlGenerator = shortUrlGenerator;
        this.urlValidator = urlValidator;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public ShortenResponse createShortLink(ShortenRequest req) {
        String validated = urlValidator.validate(req.getUrl());

        String shortUrl;
        boolean custom = req.getCustomShortUrl() != null && !req.getCustomShortUrl().isBlank();
        if (custom) {
            shortUrlGenerator.validateCustomShortUrl(req.getCustomShortUrl());
            shortUrl = req.getCustomShortUrl();
        } else {
            shortUrl = shortUrlGenerator.next();
        }

        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(validated);
        entity.setShortUrl(shortUrl);
        entity.setCustom(custom);
        try {
            repo.save(entity);
        } catch (DataIntegrityViolationException dup) {
            if (custom) {
                throw new IllegalArgumentException("Custom code already taken");
            }
            String retry = shortUrlGenerator.next();
            entity.setShortUrl(retry);
            repo.save(entity);
            shortUrl = retry;
        }

        String resultUrl =  baseUrl + "/" + shortUrl;
        return new ShortenResponse(resultUrl);
    }

    @Transactional
    public UrlMapping resolve(String shortUrl) {
        UrlMapping m = repo.findByShortUrl(shortUrl)
                .orElseThrow(() -> new NotFoundException("Provided short url not found"));

        repo.incrementClickCount(m.getId(), Instant.now());
        return m;
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }
}
