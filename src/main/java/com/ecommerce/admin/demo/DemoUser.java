package com.ecommerce.admin.demo;

import java.util.List;

public record DemoUser(
        String email,
        String firstName,
        String lastName,
        List<String> roles,
        List<String> warehouseCodes,
        boolean enabled) {
}
