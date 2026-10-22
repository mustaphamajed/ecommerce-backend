package com.ecommerce.admin.stock.query;

import com.ecommerce.admin.stock.domain.WarehouseRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WarehouseQueryService {

    private final WarehouseRepository repository;

    public WarehouseQueryService(WarehouseRepository repository) {
        this.repository = repository;
    }

    public List<WarehouseView> findAll() {
        return repository.findAll(Sort.by("code")).stream()
                .map(WarehouseView::from)
                .toList();
    }
}
