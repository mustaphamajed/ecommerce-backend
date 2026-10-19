package com.ecommerce.admin.common;

import com.ecommerce.admin.common.exception.InvalidSortException;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class AllowedSort {

    private AllowedSort() {
    }

    public static void check(Pageable pageable, Set<String> allowedProperties) {
        pageable.getSort().stream()
                .map(Sort.Order::getProperty)
                .filter(property -> !allowedProperties.contains(property))
                .findFirst()
                .ifPresent(property -> {
                    throw new InvalidSortException(property, allowedProperties);
                });
    }
}
