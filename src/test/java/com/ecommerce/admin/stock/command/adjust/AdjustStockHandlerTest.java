package com.ecommerce.admin.stock.command.adjust;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecommerce.admin.product.domain.Product;
import com.ecommerce.admin.stock.StockMetrics;
import com.ecommerce.admin.stock.domain.MovementReason;
import com.ecommerce.admin.stock.domain.Stock;
import com.ecommerce.admin.stock.domain.StockMovement;
import com.ecommerce.admin.stock.domain.StockMovementRepository;
import com.ecommerce.admin.stock.domain.StockProduct;
import com.ecommerce.admin.stock.domain.StockProductRepository;
import com.ecommerce.admin.stock.domain.StockRepository;
import com.ecommerce.admin.stock.domain.WarehouseAccess;
import com.ecommerce.admin.stock.exception.InsufficientStockException;
import com.ecommerce.admin.stock.exception.InvalidMovementException;
import com.ecommerce.admin.stock.exception.ProductArchivedException;
import com.ecommerce.admin.stock.exception.UnknownStockProductException;
import com.ecommerce.admin.stock.exception.WarehouseAccessDeniedException;
import com.ecommerce.admin.user.domain.User;
import com.ecommerce.admin.user.domain.UserRepository;
import com.ecommerce.admin.warehouse.domain.Warehouse;
import com.ecommerce.admin.warehouse.domain.WarehouseRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdjustStockHandlerTest {

    private static final long PRODUCT_ID = 1L;
    private static final long WAREHOUSE_ID = 10L;
    private static final long ACTOR_ID = 100L;
    private static final long MOVEMENT_ID = 999L;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockMovementRepository movementRepository;

    @Mock
    private StockProductRepository stockProducts;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WarehouseAccess warehouseAccess;

    @Mock
    private StockMetrics metrics;

    @InjectMocks
    private AdjustStockHandler handler;

    private StockProduct stockProduct;
    private Product product;
    private Warehouse warehouse;
    private User actor;

    @BeforeEach
    void setUp() {
        stockProduct = new StockProduct(PRODUCT_ID, "SHOE-001", "Running shoe", false);
        product = withId(new Product("SHOE-001", "Running shoe", null, new BigDecimal("59.90"), null), PRODUCT_ID);
        warehouse = withId(new Warehouse("PAR", "Paris"), WAREHOUSE_ID);
        actor = withId(new User("karim@shop.local", "hash", "Karim", "Haddad"), ACTOR_ID);
    }

    @Test
    void recordsAValidMovement() {
        givenProductWarehouseAndActorExist();
        when(stockRepository.applyChange(eq(PRODUCT_ID), eq(WAREHOUSE_ID), eq(5), any())).thenReturn(1);
        when(movementRepository.save(any())).thenAnswer(invocation -> withId(invocation.getArgument(0), MOVEMENT_ID));

        Long movementId = handler.handle(command(5, MovementReason.PURCHASE));

        assertThat(movementId).isEqualTo(MOVEMENT_ID);
        var order = inOrder(stockRepository);
        order.verify(stockRepository).createIfMissing(eq(PRODUCT_ID), eq(WAREHOUSE_ID), any());
        order.verify(stockRepository).applyChange(eq(PRODUCT_ID), eq(WAREHOUSE_ID), eq(5), any());

        var saved = ArgumentCaptor.forClass(StockMovement.class);
        verify(movementRepository).save(saved.capture());
        assertThat(saved.getValue().getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(saved.getValue().getQuantityChange()).isEqualTo(5);
        assertThat(saved.getValue().getReason()).isEqualTo(MovementReason.PURCHASE);
        assertThat(saved.getValue().getCreatedBy()).isSameAs(actor);
        verify(metrics).movementRecorded(MovementReason.PURCHASE, 5);
    }

    @Test
    void rejectsAMovementInTheWrongDirection() {
        assertThatThrownBy(() -> handler.handle(command(5, MovementReason.SALE)))
                .isInstanceOf(InvalidMovementException.class);

        verifyNoInteractions(stockProducts, stockRepository, movementRepository, metrics);
    }

    @Test
    void rejectsTransfersBecauseOnlyTheSystemCreatesThem() {
        assertThatThrownBy(() -> handler.handle(command(5, MovementReason.TRANSFER_IN)))
                .isInstanceOf(InvalidMovementException.class);

        verifyNoInteractions(stockRepository);
    }

    @Test
    void rejectsAnUnknownProduct() {
        when(stockProducts.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command(5, MovementReason.PURCHASE)))
                .isInstanceOf(UnknownStockProductException.class);

        verifyNoInteractions(stockRepository);
    }

    @Test
    void rejectsAnArchivedProduct() {
        when(stockProducts.findById(PRODUCT_ID))
                .thenReturn(Optional.of(new StockProduct(PRODUCT_ID, "SHOE-001", "Running shoe", true)));

        assertThatThrownBy(() -> handler.handle(command(5, MovementReason.PURCHASE)))
                .isInstanceOf(ProductArchivedException.class);

        verifyNoInteractions(stockRepository);
    }

    @Test
    void rejectsAWarehouseTheActorIsNotAssignedTo() {
        givenProductWarehouseAndActorExist();
        doThrow(new WarehouseAccessDeniedException("PAR")).when(warehouseAccess).ensureCanAdjust(actor, warehouse);

        assertThatThrownBy(() -> handler.handle(command(5, MovementReason.PURCHASE)))
                .isInstanceOf(WarehouseAccessDeniedException.class);

        verify(stockRepository, never()).applyChange(anyLong(), anyLong(), anyInt(), any());
        verifyNoInteractions(movementRepository, metrics);
    }

    @Test
    void rejectsASaleLargerThanTheStock() {
        givenProductWarehouseAndActorExist();
        when(stockRepository.applyChange(eq(PRODUCT_ID), eq(WAREHOUSE_ID), eq(-5), any())).thenReturn(0);
        var stock = new Stock(product, warehouse);
        ReflectionTestUtils.setField(stock, "quantity", 2);
        when(stockRepository.findByProductIdAndWarehouseId(PRODUCT_ID, WAREHOUSE_ID)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> handler.handle(command(-5, MovementReason.SALE)))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Not enough stock: 2 available, 5 requested");

        verifyNoInteractions(movementRepository, metrics);
    }

    private void givenProductWarehouseAndActorExist() {
        when(stockProducts.findById(PRODUCT_ID)).thenReturn(Optional.of(stockProduct));
        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse));
        when(userRepository.findByIdOrThrow(ACTOR_ID)).thenReturn(actor);
    }

    private static AdjustStockCommand command(int quantityChange, MovementReason reason) {
        return new AdjustStockCommand(PRODUCT_ID, WAREHOUSE_ID, quantityChange, reason, null, ACTOR_ID);
    }

    private static <T> T withId(T entity, long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
