package com.ecommerce.admin.common.web;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriUtils;

public final class ApiResponses {

    public static final String MESSAGE_HEADER = "X-Message";

    private ApiResponses() {
    }

    public static <T> ResponseEntity<T> ok(T body, String message) {
        return ResponseEntity.ok().header(MESSAGE_HEADER, encode(message)).body(body);
    }

    public static <T> ResponseEntity<T> created(URI location, T body, String message) {
        return ResponseEntity.created(location).header(MESSAGE_HEADER, encode(message)).body(body);
    }

    public static ResponseEntity<Void> noContent(String message) {
        return ResponseEntity.noContent().header(MESSAGE_HEADER, encode(message)).build();
    }

    private static String encode(String message) {
        return UriUtils.encode(message, StandardCharsets.UTF_8);
    }
}
