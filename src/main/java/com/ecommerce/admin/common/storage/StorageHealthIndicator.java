package com.ecommerce.admin.common.storage;

import java.time.Duration;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Component("storage")
public class StorageHealthIndicator extends AbstractHealthIndicator {

    private static final Duration TIMEOUT = Duration.ofSeconds(2);

    private final S3Client s3;
    private final StorageProperties properties;

    public StorageHealthIndicator(S3Client s3, StorageProperties properties) {
        super("Object storage health check failed");
        this.s3 = s3;
        this.properties = properties;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        builder.withDetail("endpoint", properties.endpoint()).withDetail("bucket", properties.bucket());
        try {
            s3.headBucket(request -> request
                    .bucket(properties.bucket())
                    .overrideConfiguration(config -> config.apiCallTimeout(TIMEOUT)));
            builder.up();
        } catch (NoSuchBucketException e) {
            builder.down().withDetail("error", "Bucket not found");
        }
    }
}
