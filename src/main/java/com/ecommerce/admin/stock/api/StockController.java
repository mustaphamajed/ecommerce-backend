package com.ecommerce.admin.stock.api;

import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.web.ApiResponses;
import com.ecommerce.admin.stock.command.adjust.AdjustStockCommand;
import com.ecommerce.admin.stock.command.adjust.AdjustStockHandler;
import com.ecommerce.admin.stock.query.StockLevelView;
import com.ecommerce.admin.stock.query.StockMovementView;
import com.ecommerce.admin.stock.query.StockQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stock", description = "Stock levels per warehouse, adjustments and history")
@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final AdjustStockHandler adjustStock;
    private final StockQueryService queries;

    public StockController(AdjustStockHandler adjustStock, StockQueryService queries) {
        this.adjustStock = adjustStock;
        this.queries = queries;
    }

    @GetMapping("/levels")
    public PageResponse<StockLevelView> levels(
            @RequestParam Long warehouseId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer maxQuantity,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return queries.findLevels(warehouseId, q, maxQuantity, pageable);
    }

    @GetMapping("/movements")
    public PageResponse<StockMovementView> movements(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {
        return queries.findMovements(productId, warehouseId, pageable);
    }

    @PostMapping("/adjustments")
    public ResponseEntity<StockMovementView> adjust(@Valid @RequestBody AdjustStockRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long movementId = adjustStock.handle(new AdjustStockCommand(
                request.productId(),
                request.warehouseId(),
                request.quantityChange(),
                request.reason(),
                request.note(),
                Long.valueOf(jwt.getSubject())));
        var movement = queries.findMovement(movementId);
        String change = movement.quantityChange() > 0 ? "+" + movement.quantityChange() : String.valueOf(movement.quantityChange());
        return ApiResponses.created(
                URI.create("/api/stock/movements/" + movementId),
                movement,
                "Stock of " + movement.sku() + " in " + movement.warehouseCode() + ": " + change);
    }
}
