package com.ecommerce.admin.dashboard.api;

import com.ecommerce.admin.dashboard.query.DashboardQueryService;
import com.ecommerce.admin.dashboard.query.DashboardView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardQueryService queries;

    public DashboardController(DashboardQueryService queries) {
        this.queries = queries;
    }

    @GetMapping
    public DashboardView dashboard(Authentication authentication) {
        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return queries.findDashboard(authorities);
    }
}
