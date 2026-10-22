package com.ecommerce.admin.product.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductTest {

    private static Product newProduct() {
        return new Product("SHOE-001", "Running shoe", "Light and fast", new BigDecimal("59.90"), null);
    }

    @Nested
    class Status {

        @Test
        void newProductIsActive() {
            var product = newProduct();

            assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(product.isArchived()).isFalse();
        }

        @Test
        void archiveHidesTheProduct() {
            var product = newProduct();

            product.archive();

            assertThat(product.getStatus()).isEqualTo(ProductStatus.ARCHIVED);
            assertThat(product.isArchived()).isTrue();
        }

        @Test
        void restoreBringsAnArchivedProductBack() {
            var product = newProduct();
            product.archive();

            product.restore();

            assertThat(product.isArchived()).isFalse();
        }
    }

    @Nested
    class Image {

        @Test
        void newProductHasNoImage() {
            assertThat(newProduct().getImageKey()).isEmpty();
        }

        @Test
        void firstImageHasNoPreviousKey() {
            var product = newProduct();

            var previous = product.replaceImage("products/1/a.jpg");

            assertThat(previous).isEmpty();
            assertThat(product.getImageKey()).contains("products/1/a.jpg");
        }

        @Test
        void replacingAnImageReturnsTheOldKey() {
            var product = newProduct();
            product.replaceImage("products/1/a.jpg");

            var previous = product.replaceImage("products/1/b.png");

            assertThat(previous).contains("products/1/a.jpg");
            assertThat(product.getImageKey()).contains("products/1/b.png");
        }

        @Test
        void removingTheImageReturnsItsKeyAndClearsIt() {
            var product = newProduct();
            product.replaceImage("products/1/a.jpg");

            var removed = product.removeImage();

            assertThat(removed).contains("products/1/a.jpg");
            assertThat(product.getImageKey()).isEmpty();
        }

        @Test
        void removingWhenThereIsNoImageDoesNothing() {
            var product = newProduct();

            assertThat(product.removeImage()).isEmpty();
            assertThat(product.getImageKey()).isEmpty();
        }
    }

    @Test
    void updateReplacesTheEditableFields() {
        var product = newProduct();

        product.update("SHOE-002", "Trail shoe", null, new BigDecimal("79.00"), null);

        assertThat(product.getSku()).isEqualTo("SHOE-002");
        assertThat(product.getName()).isEqualTo("Trail shoe");
        assertThat(product.getDescription()).isNull();
        assertThat(product.getPrice()).isEqualByComparingTo("79.00");
    }
}
