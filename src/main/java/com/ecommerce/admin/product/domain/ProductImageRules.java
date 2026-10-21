package com.ecommerce.admin.product.domain;

import com.ecommerce.admin.product.exception.InvalidImageException;
import java.util.UUID;

public final class ProductImageRules {

    public static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;

    private ProductImageRules() {
    }

    public static ImageType validate(byte[] content) {
        if (content.length == 0) {
            throw InvalidImageException.empty();
        }
        if (content.length > MAX_SIZE_BYTES) {
            throw InvalidImageException.tooLarge(MAX_SIZE_BYTES);
        }
        return ImageType.detect(content).orElseThrow(InvalidImageException::unsupportedType);
    }

    public static String newKey(Long productId, ImageType type) {
        return "products/" + productId + "/" + UUID.randomUUID() + "." + type.extension();
    }
}
