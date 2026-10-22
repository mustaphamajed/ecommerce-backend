package com.ecommerce.admin.stock.domain;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
        name = "stock",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id"}),
        indexes = @Index(name = "idx_stock_warehouse_id", columnList = "warehouse_id"),
        check = @CheckConstraint(name = "ck_stock_quantity_not_negative", constraint = "quantity >= 0"))
public class Stock {

    public static final int LOW_STOCK_THRESHOLD = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private int quantity;

    @Version
    private Long version;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected Stock() {
    }

    public Stock(Long productId, Warehouse warehouse) {
        this.productId = productId;
        this.warehouse = warehouse;
        this.quantity = 0;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
