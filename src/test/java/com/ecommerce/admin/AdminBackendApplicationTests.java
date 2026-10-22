package com.ecommerce.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.contributor.Status;

class AdminBackendApplicationTests extends IntegrationTest {

    @Autowired
    private HealthEndpoint health;

    @Test
    void startsWithDatabaseAndStorageUp() {
        assertThat(health.health().getStatus()).isEqualTo(Status.UP);
    }
}
