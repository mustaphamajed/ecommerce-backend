package com.ecommerce.admin.product.command.image;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.common.media.MediaService;
import com.ecommerce.admin.product.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UploadProductImageHandler implements CommandHandler<UploadProductImageCommand, Void> {

    private static final String FOLDER = "products/";

    private final ProductRepository repository;
    private final MediaService media;

    public UploadProductImageHandler(ProductRepository repository, MediaService media) {
        this.repository = repository;
        this.media = media;
    }

    @Override
    public Void handle(UploadProductImageCommand command) {
        var product = repository.findByIdOrThrow(command.productId());
        String key = media.uploadImage(FOLDER + product.getId(), command.content());
        product.replaceImage(key).ifPresent(media::deleteAfterCommit);
        return null;
    }
}
