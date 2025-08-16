package com.example.lss.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ShortenRequest {
    @NotBlank(message = "url is required")
    private String url;

    // Optional; if present must match slug regex
    @Pattern(regexp = "^[A-Za-z0-9_-]{3,30}$", message = "custom ShortUrl must match ^[A-Za-z0-9_-]{3,30}$")
    private String customShortUrl;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomShortUrl() {
        return customShortUrl;
    }
    public void setCustomShortUrl(String customShortUrl) {
        this.customShortUrl = customShortUrl;
    }
}
