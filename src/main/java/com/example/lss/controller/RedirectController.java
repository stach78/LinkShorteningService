package com.example.lss.controller;
import com.example.lss.service.LinkShorteningService;
import com.example.lss.service.LinkShorteningService.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class RedirectController {

    private final LinkShorteningService linkShorteningService;

    public RedirectController(LinkShorteningService linkShorteningService) {
        this.linkShorteningService = linkShorteningService;
    }

    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        try {
            var mapping = linkShorteningService.resolve(shortUrl);
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", mapping.getOriginalUrl());
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found");
        }
    }
}
