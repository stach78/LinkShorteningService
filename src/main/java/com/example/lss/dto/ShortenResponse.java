package com.example.lss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShortenResponse {

    private String shortenedUrl;

    public ShortenResponse(String shortenedUrl) {
        this.shortenedUrl = shortenedUrl;
    }

    @JsonProperty("shortened_url")
    public String getShortenedUrl() {
        return shortenedUrl;
    }
}
