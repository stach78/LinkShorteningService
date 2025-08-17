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

    static class ErrorDto {
        public final String error;
        ErrorDto(String e) { this.error = e; }
    }
}
