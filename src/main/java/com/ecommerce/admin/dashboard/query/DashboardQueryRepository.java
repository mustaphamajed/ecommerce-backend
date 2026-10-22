package com.ecommerce.admin.dashboard.query;

import com.ecommerce.admin.product.domain.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface DashboardQueryRepository extends Repository<Product, Long> {

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = :enabled")
    long countUsers(boolean enabled);
}
