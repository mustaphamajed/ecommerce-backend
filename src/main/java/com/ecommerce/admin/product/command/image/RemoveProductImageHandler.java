package com.ecommerce.admin.product.command.image;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.common.media.MediaService;
import com.ecommerce.admin.product.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RemoveProductImageHandler implements CommandHandler<RemoveProductImageCommand, Void> {

    private final ProductRepository repository;
    private final MediaService media;

    public RemoveProductImageHandler(ProductRepository repository, MediaService media) {
        this.repository = repository;
        this.media = media;
    }

    @Override
    public Void handle(RemoveProductImageCommand command) {
        repository.findByIdOrThrow(command.productId())
                .removeImage()
                .ifPresent(media::deleteAfterCommit);
        return null;
    }
}
