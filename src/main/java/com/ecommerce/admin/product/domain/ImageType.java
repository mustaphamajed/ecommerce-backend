package com.ecommerce.admin.product.domain;

import java.util.Arrays;
import java.util.Optional;

public enum ImageType {
    JPEG("image/jpeg", "jpg", new int[] {0xFF, 0xD8, 0xFF}),
    PNG("image/png", "png", new int[] {0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}),
    WEBP("image/webp", "webp", null);

    private static final int[] RIFF = {0x52, 0x49, 0x46, 0x46};
    private static final int[] WEBP_MARKER = {0x57, 0x45, 0x42, 0x50};

    private final String contentType;
    private final String extension;
    private final int[] signature;

    ImageType(String contentType, String extension, int[] signature) {
        this.contentType = contentType;
        this.extension = extension;
        this.signature = signature;
    }

    public String contentType() {
        return contentType;
    }

    public String extension() {
        return extension;
    }

    public static Optional<ImageType> detect(byte[] content) {
        if (startsWith(content, 0, RIFF) && startsWith(content, 8, WEBP_MARKER)) {
            return Optional.of(WEBP);
        }
        return Arrays.stream(values())
                .filter(type -> type.signature != null && startsWith(content, 0, type.signature))
                .findFirst();
    }

    private static boolean startsWith(byte[] content, int offset, int[] expected) {
        if (content.length < offset + expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if ((content[offset + i] & 0xFF) != expected[i]) {
                return false;
            }
        }
        return true;
    }
}
