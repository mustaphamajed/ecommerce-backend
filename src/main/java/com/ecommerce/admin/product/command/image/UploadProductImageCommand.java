package com.ecommerce.admin.product.command.image;

public record UploadProductImageCommand(Long productId, byte[] content) {
}
