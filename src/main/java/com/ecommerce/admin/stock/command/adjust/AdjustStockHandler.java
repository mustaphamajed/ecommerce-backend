package com.ecommerce.admin.stock.command.adjust;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.stock.StockMetrics;
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
import com.ecommerce.admin.stock.exception.UnknownWarehouseException;
import com.ecommerce.admin.user.domain.UserRepository;
import com.ecommerce.admin.warehouse.domain.Warehouse;
import com.ecommerce.admin.warehouse.domain.WarehouseRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdjustStockHandler implements CommandHandler<AdjustStockCommand, Long> {

    private final StockRepository stockRepository;
    private final StockMovementRepository movementRepository;
    private final StockProductRepository stockProducts;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final WarehouseAccess warehouseAccess;
    private final StockMetrics metrics;

    public AdjustStockHandler(
            StockRepository stockRepository,
            StockMovementRepository movementRepository,
            StockProductRepository stockProducts,
            WarehouseRepository warehouseRepository,
            UserRepository userRepository,
            WarehouseAccess warehouseAccess,
            StockMetrics metrics) {
        this.stockRepository = stockRepository;
        this.movementRepository = movementRepository;
        this.stockProducts = stockProducts;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.warehouseAccess = warehouseAccess;
        this.metrics = metrics;
    }

    @Override
    public Long handle(AdjustStockCommand command) {
        validateMovement(command);
        var product = findActiveProduct(command.productId());
        var warehouse = findWarehouse(command.warehouseId());
        var actor = userRepository.findByIdOrThrow(command.actorId());
        warehouseAccess.ensureCanAdjust(actor, warehouse);

        var now = Instant.now();
        stockRepository.createIfMissing(product.getId(), warehouse.getId(), now);
        int updatedRows = stockRepository.applyChange(product.getId(), warehouse.getId(), command.quantityChange(), now);
        if (updatedRows == 0) {
            throw new InsufficientStockException(availableQuantity(product, warehouse), -command.quantityChange());
        }

        var movement = new StockMovement(
                product.getId(), warehouse, command.quantityChange(), command.reason(), command.note(), actor);
        Long id = movementRepository.save(movement).getId();
        metrics.movementRecorded(command.reason(), command.quantityChange());
        return id;
    }

    private void validateMovement(AdjustStockCommand command) {
        if (!command.reason().isManual()) {
            throw InvalidMovementException.notManual(command.reason());
        }
        if (!command.reason().accepts(command.quantityChange())) {
            throw InvalidMovementException.wrongDirection(command.reason(), command.quantityChange());
        }
    }

    private StockProduct findActiveProduct(Long productId) {
        var product = stockProducts.findById(productId)
                .orElseThrow(() -> new UnknownStockProductException(productId));
        if (product.isArchived()) {
            throw new ProductArchivedException(product.getSku());
        }
        return product;
    }

    private Warehouse findWarehouse(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new UnknownWarehouseException(warehouseId));
    }

    private int availableQuantity(StockProduct product, Warehouse warehouse) {
        return stockRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .map(Stock::getQuantity)
                .orElse(0);
    }
}
