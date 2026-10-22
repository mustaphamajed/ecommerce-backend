package com.ecommerce.admin;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
public abstract class IntegrationTest {

    private static final String STORAGE_ACCESS_KEY = "test-access";
    private static final String STORAGE_SECRET_KEY = "test-secret-key";
    private static final int STORAGE_PORT = 9000;

    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17");

    static final GenericContainer<?> RUSTFS = new GenericContainer<>("rustfs/rustfs:1.0.3")
            .withEnv("RUSTFS_ACCESS_KEY", STORAGE_ACCESS_KEY)
            .withEnv("RUSTFS_SECRET_KEY", STORAGE_SECRET_KEY)
            .withEnv("RUSTFS_ADDRESS", ":" + STORAGE_PORT)
            .withExposedPorts(STORAGE_PORT)
            .waitingFor(Wait.forHttp("/health").forPort(STORAGE_PORT));

    static {
        POSTGRES.start();
        RUSTFS.start();
    }

    @DynamicPropertySource
    static void storageProperties(DynamicPropertyRegistry registry) {
        registry.add("app.storage.endpoint",
                () -> "http://" + RUSTFS.getHost() + ":" + RUSTFS.getMappedPort(STORAGE_PORT));
        registry.add("app.storage.access-key", () -> STORAGE_ACCESS_KEY);
        registry.add("app.storage.secret-key", () -> STORAGE_SECRET_KEY);
        registry.add("app.seed.demo-data", () -> "false");
    }
}
