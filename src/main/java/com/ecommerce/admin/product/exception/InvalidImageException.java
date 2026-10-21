package com.ecommerce.admin.product.exception;

import com.ecommerce.admin.common.exception.BadRequestException;

public class InvalidImageException extends BadRequestException {

    private InvalidImageException(String code, String message) {
        super(code, message, "image");
    }

    public static InvalidImageException empty() {
        return new InvalidImageException("IMAGE_EMPTY", "The image file is empty");
    }

    public static InvalidImageException unsupportedType() {
        return new InvalidImageException("IMAGE_TYPE_NOT_SUPPORTED", "Only JPEG, PNG and WebP images are accepted");
    }

    public static InvalidImageException tooLarge(long maxBytes) {
        return new InvalidImageException("IMAGE_TOO_LARGE", "The image must be " + maxBytes / 1024 / 1024 + " MB or less");
    }
}
