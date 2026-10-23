package com.ecommerce.admin.stock.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.ecommerce.admin.IntegrationTest;
import com.ecommerce.admin.product.command.create.CreateProductCommand;
import com.ecommerce.admin.product.command.create.CreateProductHandler;
import com.ecommerce.admin.stock.domain.StockProductRepository;
import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.AopTestUtils;

class ProductChangedRetryTest extends IntegrationTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @MockitoSpyBean
    private ProductChangedListener listener;

    @Autowired
    private CreateProductHandler createProduct;

    @Autowired
    private StockProductRepository stockProducts;

    @Autowired
    private IncompleteEventPublications incompletePublications;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void aFailedEventIsKeptAndCanBeResubmitted() {
        ProductChangedListener realListener = AopTestUtils.getUltimateTargetObject(listener);
        doThrow(new IllegalStateException("simulated listener crash"))
                .doCallRealMethod()
                .when(realListener).on(any());

        Long productId = createProduct.handle(
                new CreateProductCommand("RETRY-001", "Retry test", null, BigDecimal.ONE, null));

        await().atMost(TIMEOUT).until(() -> incompleteEventsFor("RETRY-001") == 1);
        assertThat(stockProducts.existsById(productId)).isFalse();

        incompletePublications.resubmitIncompletePublications(publication -> true);

        await().atMost(TIMEOUT).until(() -> stockProducts.existsById(productId));
        await().atMost(TIMEOUT).until(() -> storedEventsFor("RETRY-001") == 0);
    }

    private int incompleteEventsFor(String sku) {
        return countEvents("completion_date IS NULL AND serialized_event LIKE ?", sku);
    }

    private int storedEventsFor(String sku) {
        return countEvents("serialized_event LIKE ?", sku);
    }

    private int countEvents(String condition, String sku) {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM event_publication WHERE " + condition,
                Integer.class,
                "%\"sku\":\"" + sku + "\"%");
    }
}
