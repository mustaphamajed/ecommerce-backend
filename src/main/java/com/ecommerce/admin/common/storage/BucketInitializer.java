package com.ecommerce.admin.common.storage;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Component
@Order(0)
public class BucketInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BucketInitializer.class);
    private static final int MAX_ATTEMPTS = 15;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);

    private final S3Client s3;
    private final String bucket;

    public BucketInitializer(S3Client s3, StorageProperties properties) {
        this.s3 = s3;
        this.bucket = properties.bucket();
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                ensureBucketExists();
                return;
            } catch (SdkClientException e) {
                log.warn("Object storage not reachable yet (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
                Thread.sleep(RETRY_DELAY);
            }
        }
        throw new IllegalStateException("Object storage is not reachable, check app.storage.endpoint");
    }

    private void ensureBucketExists() {
        try {
            s3.headBucket(request -> request.bucket(bucket));
            log.info("Using storage bucket {}", bucket);
        } catch (NoSuchBucketException e) {
            s3.createBucket(request -> request.bucket(bucket));
            log.info("Created storage bucket {}", bucket);
        }
    }
}
