package com.example.lss.controller;

import com.example.lss.dto.LinkDetailsResponse;
import com.example.lss.repo.UrlMappingRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/links")
public class UserLinksController {

    private final UrlMappingRepository repo;

    public UserLinksController(UrlMappingRepository repo) { this.repo = repo; }

    @GetMapping
    public ResponseEntity<Page<LinkDetailsResponse>> userLinks(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createDate"));
        var pageData = repo.findByOwner_Username(principal.getUsername(), pageable)
                .map(m -> new LinkDetailsResponse(
                        m.getShortUrl(), m.getOriginalUrl(), m.getClickCount(), m.getCreateDate(), m.getLastAccessed()
                ));
        return ResponseEntity.ok(pageData);
    }

    @DeleteMapping("/{shortUrl}")
    @Transactional
    public ResponseEntity<?> delete(
            @PathVariable String shortUrl,
            @AuthenticationPrincipal UserDetails principal) {
        boolean exists = repo.existsByShortUrlAndOwner_Username(shortUrl, principal.getUsername());
        if (!exists) return ResponseEntity.notFound().build();
        repo.deleteByShortUrlAndOwner_Username(shortUrl, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
