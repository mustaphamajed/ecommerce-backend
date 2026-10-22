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

    @Column(length = 100)
    private String categoryName;

    @Column(nullable = false)
    private boolean archived;

    protected StockProduct() {
    }

    public StockProduct(Long id, String sku, String name, String categoryName, boolean archived) {
        this.id = id;
        update(sku, name, categoryName, archived);
    }

    public void update(String sku, String name, String categoryName, boolean archived) {
        this.sku = sku;
        this.name = name;
        this.categoryName = categoryName;
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

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isArchived() {
        return archived;
    }
}
