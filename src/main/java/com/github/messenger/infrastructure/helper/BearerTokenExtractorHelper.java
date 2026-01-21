package com.github.messenger.infrastructure.helper;

import org.springframework.stereotype.Component;

@Component
public class BearerTokenExtractorHelper {
    public String extract(String token) {
        if (token != null && token.length() > 8 && token.startsWith("Bearer "))
            return token.substring(7);
        return null;
    }
}
