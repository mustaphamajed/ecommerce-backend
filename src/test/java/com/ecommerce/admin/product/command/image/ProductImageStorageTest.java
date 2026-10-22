package com.ecommerce.admin.product.command.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ecommerce.admin.IntegrationTest;
import com.ecommerce.admin.common.storage.StorageProperties;
import com.ecommerce.admin.product.domain.Product;
import com.ecommerce.admin.product.domain.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

class ProductImageStorageTest extends IntegrationTest {

    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x01};

    @Autowired
    private UploadProductImageHandler uploadImage;

    @Autowired
    private RemoveProductImageHandler removeImage;

    @Autowired
    private ProductRepository products;

    @Autowired
    private TransactionTemplate transaction;

    @Autowired
    private S3Client s3;

    @Autowired
    private StorageProperties storage;

    private Product product;

    @BeforeEach
    void createProduct() {
        String sku = "IMG-" + UUID.randomUUID().toString().substring(0, 8);
        product = products.save(new Product(sku, "Image test", null, BigDecimal.ONE, null));
    }

    @Test
    void uploadStoresTheFileAndAReplacementDeletesTheOldOne() {
        uploadImage.handle(new UploadProductImageCommand(product.getId(), PNG));
        String firstKey = savedImageKey().orElseThrow();

        uploadImage.handle(new UploadProductImageCommand(product.getId(), PNG));
        String secondKey = savedImageKey().orElseThrow();

        assertThat(secondKey).isNotEqualTo(firstKey).startsWith("products/" + product.getId() + "/").endsWith(".png");
        assertThat(storedKeys()).containsExactly(secondKey);
    }

    @Test
    void aRolledBackUploadLeavesNoFileBehind() {
        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
            uploadImage.handle(new UploadProductImageCommand(product.getId(), PNG));
            assertThat(storedKeys()).hasSize(1);
            throw new IllegalStateException("simulated failure after the upload");
        })).hasMessage("simulated failure after the upload");

        assertThat(savedImageKey()).isEmpty();
        assertThat(storedKeys()).isEmpty();
    }

    @Test
    void removingTheImageDeletesTheFile() {
        uploadImage.handle(new UploadProductImageCommand(product.getId(), PNG));

        removeImage.handle(new RemoveProductImageCommand(product.getId()));

        assertThat(savedImageKey()).isEmpty();
        assertThat(storedKeys()).isEmpty();
    }

    private Optional<String> savedImageKey() {
        return products.findById(product.getId()).orElseThrow().getImageKey();
    }

    private List<String> storedKeys() {
        return s3.listObjectsV2(request -> request
                        .bucket(storage.bucket())
                        .prefix("products/" + product.getId() + "/"))
                .contents().stream()
                .map(S3Object::key)
                .toList();
    }
}
