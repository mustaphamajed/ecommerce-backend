package com.ecommerce.admin.stock.command.adjust;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecommerce.admin.IntegrationTest;
import com.ecommerce.admin.product.domain.Product;
import com.ecommerce.admin.product.domain.ProductRepository;
import com.ecommerce.admin.stock.domain.MovementReason;
import com.ecommerce.admin.stock.domain.StockMovementRepository;
import com.ecommerce.admin.stock.domain.StockRepository;
import com.ecommerce.admin.stock.exception.InsufficientStockException;
import com.ecommerce.admin.user.domain.UserRepository;
import com.ecommerce.admin.warehouse.domain.Warehouse;
import com.ecommerce.admin.warehouse.domain.WarehouseRepository;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

class AdjustStockConcurrencyTest extends IntegrationTest {

    private static final int INITIAL_STOCK = 10;
    private static final int BUYERS = 20;

    @Autowired
    private AdjustStockHandler handler;

    @Autowired
    private ProductRepository products;

    @Autowired
    private WarehouseRepository warehouses;

    @Autowired
    private UserRepository users;

    @Autowired
    private StockRepository stock;

    @Autowired
    private StockMovementRepository movements;

    @Value("${app.bootstrap.admin.email}")
    private String adminEmail;

    @Test
    void parallelSalesNeverSellMoreThanTheStock() throws Exception {
        var product = products.save(new Product("CONC-001", "Concurrency test", null, BigDecimal.TEN, null));
        var warehouse = warehouses.save(new Warehouse("CONC", "Concurrency warehouse"));
        Long adminId = users.findByEmail(adminEmail).orElseThrow().getId();
        handler.handle(new AdjustStockCommand(
                product.getId(), warehouse.getId(), INITIAL_STOCK, MovementReason.PURCHASE, null, adminId));

        var sale = new AdjustStockCommand(product.getId(), warehouse.getId(), -1, MovementReason.SALE, null, adminId);
        var startSignal = new CountDownLatch(1);
        long successfulSales;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var results = IntStream.range(0, BUYERS)
                    .mapToObj(buyer -> executor.submit(() -> {
                        startSignal.await();
                        try {
                            handler.handle(sale);
                            return true;
                        } catch (InsufficientStockException e) {
                            return false;
                        }
                    }))
                    .toList();
            startSignal.countDown();
            successfulSales = 0;
            for (Future<Boolean> result : results) {
                if (result.get()) {
                    successfulSales++;
                }
            }
        }

        assertThat(successfulSales).isEqualTo(INITIAL_STOCK);
        assertThat(stock.findByProductIdAndWarehouseId(product.getId(), warehouse.getId()))
                .hasValueSatisfying(level -> assertThat(level.getQuantity()).isZero());
        assertThat(movements.findAll())
                .filteredOn(movement -> movement.getProduct().getId().equals(product.getId()))
                .hasSize(1 + INITIAL_STOCK);
    }
}
