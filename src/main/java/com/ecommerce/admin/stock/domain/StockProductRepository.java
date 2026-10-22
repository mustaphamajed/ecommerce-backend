package com.ecommerce.admin.stock.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockProductRepository extends JpaRepository<StockProduct, Long> {
}
