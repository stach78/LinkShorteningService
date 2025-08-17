package com.example.lss.controller;
import com.example.lss.dto.ShortenRequest;
import com.example.lss.dto.ShortenResponse;
import com.example.lss.entity.UrlMapping;
import com.example.lss.repo.UrlMappingRepository;
import com.example.lss.service.LinkShorteningService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class LinkApiController {

    private final LinkShorteningService linkShorteningService;
    private final UrlMappingRepository repo;

    public LinkApiController(LinkShorteningService linkShorteningService, UrlMappingRepository repo) {
        this.linkShorteningService = linkShorteningService;
        this.repo = repo;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(@RequestBody @Valid ShortenRequest req,
                                                   @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal) {
        ShortenResponse resp = linkShorteningService.createShortLink(req, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/api/links/{shortUrl}")
    public ResponseEntity<?> getLink(@PathVariable String shortUrl) {
        return repo.findByShortUrl(shortUrl)
                .<ResponseEntity<?>>map(m -> ResponseEntity.ok(new LinkDetails(m)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto("Not found")));
    }

    static class LinkDetails {
        public final String code;
        public final String original_url;
        public final long click_count;
        public final java.time.Instant created_at;
        public final java.time.Instant last_accessed_at;

        LinkDetails(UrlMapping m) {
            this.code = m.getShortUrl();
            this.original_url = m.getOriginalUrl();
            this.click_count = m.getClickCount();
            this.created_at = m.getCreateDate();
            this.last_accessed_at = m.getLastAccessed();
        }
    }

    static class ErrorDto {
        public final String error;
        ErrorDto(String e) { this.error = e; }
    }
}
