package com.ecommerce.admin.common.media;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Media (public)", description = "Uploaded images, served with long-lived caching")
@RestController
@RequestMapping("/api/media")
public class MediaController {

    private static final Duration CACHE_DURATION = Duration.ofDays(365);

    private final MediaService media;

    public MediaController(MediaService media) {
        this.media = media;
    }

    @GetMapping("/{*key}")
    public ResponseEntity<byte[]> get(@PathVariable String key) {
        var file = media.load(key.startsWith("/") ? key.substring(1) : key);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .cacheControl(CacheControl.maxAge(CACHE_DURATION).cachePublic().immutable())
                .header("X-Content-Type-Options", "nosniff")
                .body(file.content());
    }
}
