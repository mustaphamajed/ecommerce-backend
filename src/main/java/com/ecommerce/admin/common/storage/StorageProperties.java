package com.ecommerce.admin.common.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.storage")
public record StorageProperties(String endpoint, String region, String accessKey, String secretKey, String bucket) {
}
