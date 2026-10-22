package com.ecommerce.admin.stock.listener;

import com.ecommerce.admin.product.ProductChanged;
import com.ecommerce.admin.stock.domain.StockProduct;
import com.ecommerce.admin.stock.domain.StockProductRepository;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class ProductChangedListener {

    private final StockProductRepository products;

    public ProductChangedListener(StockProductRepository products) {
        this.products = products;
    }

    @ApplicationModuleListener
    void on(ProductChanged event) {
        products.findById(event.productId()).ifPresentOrElse(
                product -> product.update(event.sku(), event.name(), event.categoryName(), event.archived()),
                () -> products.save(new StockProduct(
                        event.productId(), event.sku(), event.name(), event.categoryName(), event.archived())));
    }
}
