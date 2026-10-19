package com.ecommerce.admin.stock;

import com.ecommerce.admin.product.Product;
import com.ecommerce.admin.user.domain.User;
import com.ecommerce.admin.warehouse.domain.Warehouse;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected StockMovement() {
    }

    public StockMovement(Product product, Warehouse warehouse, int quantityChange,
            MovementReason reason, String note, User createdBy) {
        this.product = product;
        this.warehouse = warehouse;
        this.quantityChange = quantityChange;
        this.reason = reason;
        this.note = note;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
