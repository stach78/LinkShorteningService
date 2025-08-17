package com.example.lss.controller;

import com.example.lss.dto.LinkDetailsResponse;
import com.example.lss.entity.UrlMapping;
import com.example.lss.service.LinkShorteningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class LinkQueryController {

    private final LinkShorteningService service;

    public LinkQueryController(LinkShorteningService service) {
        this.service = service;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<LinkDetailsResponse> getDetails(@PathVariable String shortUrl) {
        UrlMapping m = service.getMetadata(shortUrl);
        return ResponseEntity.ok(new LinkDetailsResponse(
                m.getShortUrl(),
                m.getOriginalUrl(),
                m.getClickCount(),
                m.getCreateDate(),
                m.getLastAccessed()
        ));
    }
}
