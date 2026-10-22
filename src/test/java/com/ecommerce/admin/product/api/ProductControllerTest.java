package com.ecommerce.admin.product.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.config.CorsConfig;
import com.ecommerce.admin.common.security.ApiAccessDeniedHandler;
import com.ecommerce.admin.common.security.ApiAuthenticationEntryPoint;
import com.ecommerce.admin.common.security.JwtConfig;
import com.ecommerce.admin.common.security.SecurityConfig;
import com.ecommerce.admin.common.web.ApiResponses;
import com.ecommerce.admin.product.command.archive.ArchiveProductHandler;
import com.ecommerce.admin.product.command.create.CreateProductCommand;
import com.ecommerce.admin.product.command.create.CreateProductHandler;
import com.ecommerce.admin.product.command.image.RemoveProductImageHandler;
import com.ecommerce.admin.product.command.image.UploadProductImageHandler;
import com.ecommerce.admin.product.command.restore.RestoreProductHandler;
import com.ecommerce.admin.product.command.update.UpdateProductHandler;
import com.ecommerce.admin.product.domain.ProductStatus;
import com.ecommerce.admin.product.exception.DuplicateSkuException;
import com.ecommerce.admin.product.exception.ProductNotFoundException;
import com.ecommerce.admin.product.query.ProductQueryService;
import com.ecommerce.admin.product.query.ProductView;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtConfig.class, CorsConfig.class,
        ApiAuthenticationEntryPoint.class, ApiAccessDeniedHandler.class})
class ProductControllerTest {

    private static final String VALID_PRODUCT = """
            {"sku": "SHOE-001", "name": "Running shoe", "description": null, "price": 59.90, "categoryId": null}
            """;

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private CreateProductHandler createProduct;

    @MockitoBean
    private UpdateProductHandler updateProduct;

    @MockitoBean
    private ArchiveProductHandler archiveProduct;

    @MockitoBean
    private RestoreProductHandler restoreProduct;

    @MockitoBean
    private UploadProductImageHandler uploadImage;

    @MockitoBean
    private RemoveProductImageHandler removeImage;

    @MockitoBean
    private ProductQueryService queries;

    @Test
    void anonymousUsersGet401InTheApiErrorFormat() {
        assertThat(mvc.get().uri("/api/products"))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.errors[0].code").isEqualTo("UNAUTHENTICATED");
    }

    @Test
    void readersCanListProducts() {
        when(queries.search(any(), any(), any(), any()))
                .thenReturn(new PageResponse<>(List.of(productView()), 0, 10, 1, 1));

        assertThat(mvc.get().uri("/api/products").with(as("product:read")))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.content[0].sku").isEqualTo("SHOE-001");
    }

    @Test
    void readersCannotCreateProducts() {
        assertThat(mvc.post().uri("/api/products").with(as("product:read"))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_PRODUCT))
                .hasStatus(HttpStatus.FORBIDDEN)
                .bodyJson()
                .extractingPath("$.errors[0].code").isEqualTo("FORBIDDEN");

        verifyNoInteractions(createProduct);
    }

    @Test
    void writersCreateAProductWithLocationAndMessage() {
        when(createProduct.handle(any(CreateProductCommand.class))).thenReturn(42L);
        when(queries.findById(42L)).thenReturn(productView());

        var result = mvc.post().uri("/api/products").with(as("product:write"))
                .contentType(MediaType.APPLICATION_JSON).content(VALID_PRODUCT)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.CREATED).hasHeader("Location", "/api/products/42");
        assertThat(decodedMessage(result.getResponse().getHeader(ApiResponses.MESSAGE_HEADER)))
                .isEqualTo("Product SHOE-001 created");
    }

    @Test
    void invalidFieldsAreReportedOneByOne() {
        String invalid = """
                {"sku": "bad sku!", "name": "", "description": null, "price": 0, "categoryId": null}
                """;

        assertThat(mvc.post().uri("/api/products").with(as("product:write"))
                        .contentType(MediaType.APPLICATION_JSON).content(invalid))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.errors[*].property").asArray()
                .containsExactlyInAnyOrder("sku", "name", "price");

        verifyNoInteractions(createProduct);
    }

    @Test
    void aDuplicateSkuIsAConflict() {
        when(createProduct.handle(any(CreateProductCommand.class))).thenThrow(new DuplicateSkuException("SHOE-001"));

        assertThat(mvc.post().uri("/api/products").with(as("product:write"))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_PRODUCT))
                .hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void anUnknownProductIsA404() {
        when(queries.findById(99L)).thenThrow(new ProductNotFoundException(99L));

        assertThat(mvc.get().uri("/api/products/99").with(as("product:read")))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.errors[0].code").isEqualTo("PRODUCT_NOT_FOUND");
    }

    private static JwtRequestPostProcessor as(String... permissions) {
        return jwt().authorities(Arrays.stream(permissions)
                .map(SimpleGrantedAuthority::new)
                .toArray(GrantedAuthority[]::new));
    }

    private static String decodedMessage(String header) {
        return URLDecoder.decode(header, StandardCharsets.UTF_8);
    }

    private static ProductView productView() {
        var now = Instant.parse("2026-10-22T10:00:00Z");
        return new ProductView(42L, "SHOE-001", "Running shoe", null, new BigDecimal("59.90"),
                ProductStatus.ACTIVE, null, null, null, now, now);
    }
}
