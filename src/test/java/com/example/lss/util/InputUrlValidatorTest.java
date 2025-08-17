package com.example.lss.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputUrlValidatorTest {

    private final InputUrlValidator iuv = new InputUrlValidator();

    @Test
    void lowercasesSchemeAndHostKeepsPathAndQuery() {
        String out = iuv.validate("HTTPS://ExAmPlE.com/Path/Sub?q=1");
        assertEquals("https://example.com/Path/Sub?q=1", out);
    }

    @Test
    void useHttpDefaultIfNoScheme() {
        String out = iuv.validate("example.com");
        assertEquals("http://example.com", out);
    }

    @Test
    void rejectsNonHttpSchemes() {
        assertThrows(IllegalArgumentException.class, () -> iuv.validate("ftp://example.com"));
        assertThrows(IllegalArgumentException.class, () -> iuv.validate("javascript:alert(1)"));
    }

    @Test
    void trimsWhitespace() {
        String out = iuv.validate("   https://example.com  ");
        assertEquals("https://example.com", out);
    }

    @Test
    void requiresHost() {
        assertThrows(IllegalArgumentException.class, () -> iuv.validate("https:///nohost"));
    }
}
