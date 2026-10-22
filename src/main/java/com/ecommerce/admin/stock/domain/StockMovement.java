package com.ecommerce.admin.stock.domain;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
        name = "stock_movements",
        indexes = @Index(name = "idx_stock_movements_product", columnList = "product_id, created_at DESC"),
        check = @CheckConstraint(name = "ck_stock_movements_change_not_zero", constraint = "quantity_change <> 0"))
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

   
    @Column(nullable = false)
    private int quantityChange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MovementReason reason;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "created_by")
    private Long createdById;

    @Column(name = "created_by_name", length = 201)
    private String createdByName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected StockMovement() {
    }

    public StockMovement(Long productId, Warehouse warehouse, int quantityChange,
            MovementReason reason, String note, Long createdById, String createdByName) {
        this.productId = productId;
        this.warehouse = warehouse;
        this.quantityChange = quantityChange;
        this.reason = reason;
        this.note = note;
        this.createdById = createdById;
        this.createdByName = createdByName;
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

    public int getQuantityChange() {
        return quantityChange;
    }

    public MovementReason getReason() {
        return reason;
    }

    public String getNote() {
        return note;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
