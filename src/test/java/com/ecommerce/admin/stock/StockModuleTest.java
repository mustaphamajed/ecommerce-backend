package com.ecommerce.admin.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecommerce.admin.IntegrationTest;
import com.ecommerce.admin.events.ProductChanged;
import com.ecommerce.admin.stock.domain.StockProduct;
import com.ecommerce.admin.stock.domain.StockProductRepository;
import com.ecommerce.admin.user.ActorLookup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ApplicationModuleTest(verifyAutomatically = false)
class StockModuleTest {

    @MockitoBean
    private ActorLookup actors;

    @Autowired
    private StockProductRepository stockProducts;

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", IntegrationTest.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", IntegrationTest.POSTGRES::getUsername);
        registry.add("spring.datasource.password", IntegrationTest.POSTGRES::getPassword);
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Test
    void keepsItsProductCopyUpToDate(Scenario scenario) {
        scenario.publish(new ProductChanged(9001L, "MOD-001", "Module test", "Shoes", false))
                .andWaitForStateChange(() -> stockProducts.findById(9001L))
                .andVerify(copy -> assertThat(copy).get()
                        .extracting(StockProduct::getName, StockProduct::getCategoryName, StockProduct::isArchived)
                        .containsExactly("Module test", "Shoes", false));
    }
}
