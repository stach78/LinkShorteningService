package com.example.lss.util;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/*
this class validates user input:
trims whitespace
converts host part to lowercase
allows only http/https, adds http if protocol not provided

 */

@Component
public class InputUrlValidator {
    private static final Set<String> ALLOWED_PROTOCOLS = Set.of("https", "http");

    public String validate(String inputUrl){
        if(inputUrl == null || inputUrl.isBlank()){
            throw new IllegalArgumentException("URL cannot be empty");
        }
        String trimmed = inputUrl.trim();
        URI uri = parse(inputUrl);
        String scheme = uri.getScheme();
        if (!ALLOWED_PROTOCOLS.contains(scheme)) {
            throw new IllegalArgumentException("Only http/https URLs are allowed");
        }

        String host = Optional.ofNullable(uri.getHost())
                .orElseThrow(() -> new IllegalArgumentException("URL must include host")).toLowerCase(Locale.ROOT);

        int port = uri.getPort();
        String authority = port == -1 ? host : host + ":" + port;

        String rawPath = Optional.ofNullable(uri.getRawPath()).orElse("");
        String rawQuery = uri.getRawQuery();

        try {
            URI validated = new URI(scheme, authority, rawPath, rawQuery, null);
            return validated.toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL", e);
        }
    }

    private URI parse(String input) {
        try {
            URI uri = new URI(input);
            String scheme = uri.getScheme();
            if (scheme == null) {
                return new URI("http://" + input);
            } else if (!scheme.equals("http") && !scheme.equals("https")) { //disallow e. g. ftp, javascript
                throw new IllegalArgumentException("Only http/https URLs are allowed");
            }

            return uri;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL", e);
        }
    }
}


