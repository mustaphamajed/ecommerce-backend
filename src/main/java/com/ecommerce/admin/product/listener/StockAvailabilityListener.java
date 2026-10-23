package com.ecommerce.admin.product.listener;

import com.ecommerce.admin.events.StockAvailabilityChanged;
import com.ecommerce.admin.product.domain.ProductRepository;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class StockAvailabilityListener {

    private final ProductRepository products;

    public StockAvailabilityListener(ProductRepository products) {
        this.products = products;
    }

    @ApplicationModuleListener
    void on(StockAvailabilityChanged event) {
        products.updateAvailability(event.productId(), event.inStock());
    }
}
