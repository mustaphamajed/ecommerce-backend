package com.ecommerce.admin.stock.query;

import com.ecommerce.admin.common.AllowedSort;
import com.ecommerce.admin.common.PageResponse;
import com.ecommerce.admin.common.SearchPatterns;
import com.ecommerce.admin.stock.exception.UnknownWarehouseException;
import com.ecommerce.admin.warehouse.domain.WarehouseRepository;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StockQueryService {

    private static final Set<String> LEVEL_SORTABLE_PROPERTIES = Set.of("name", "sku", "quantity");

    private final StockQueryRepository repository;
    private final WarehouseRepository warehouseRepository;

    public StockQueryService(StockQueryRepository repository, WarehouseRepository warehouseRepository) {
        this.repository = repository;
        this.warehouseRepository = warehouseRepository;
    }

    public PageResponse<StockLevelView> findLevels(
            Long warehouseId, String query, Integer maxQuantity, Pageable pageable) {
        AllowedSort.check(pageable, LEVEL_SORTABLE_PROPERTIES);
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new UnknownWarehouseException(warehouseId);
        }
        return PageResponse.from(repository.findLevels(
                warehouseId, SearchPatterns.contains(query), maxQuantity, toLevelSort(pageable)));
    }

    public PageResponse<StockMovementView> findMovements(Long productId, Long warehouseId, Pageable pageable) {
        var newestFirst = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return PageResponse.from(repository.findMovements(productId, warehouseId, newestFirst));
    }

    public StockMovementView findMovement(Long id) {
        return repository.findMovementById(id)
                .orElseThrow(() -> new IllegalStateException("Movement " + id + " was just created but can't be read"));
    }

    private Pageable toLevelSort(Pageable pageable) {
        Sort sort = Sort.unsorted();
        for (Sort.Order order : pageable.getSort()) {
            sort = sort.and(order.getProperty().equals("quantity")
                    ? JpaSort.unsafe(order.getDirection(), "COALESCE(s.quantity, 0)")
                    : Sort.by(order.getDirection(), order.getProperty()));
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
