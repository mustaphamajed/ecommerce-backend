package com.ecommerce.admin.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_products")
public class StockProduct {

    @Id
    private Long id;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private boolean archived;

    protected StockProduct() {
    }

    public StockProduct(Long id, String sku, String name, boolean archived) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.archived = archived;
    }

    public void update(String sku, String name, boolean archived) {
        this.sku = sku;
        this.name = name;
        this.archived = archived;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public boolean isArchived() {
        return archived;
    }
}
