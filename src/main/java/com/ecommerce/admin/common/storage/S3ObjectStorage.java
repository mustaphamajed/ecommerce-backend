package com.ecommerce.admin.common.storage;

import java.util.Optional;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Component
public class S3ObjectStorage implements ObjectStorage {

    private final S3Client s3;
    private final String bucket;

    public S3ObjectStorage(S3Client s3, StorageProperties properties) {
        this.s3 = s3;
        this.bucket = properties.bucket();
    }

    @Override
    public void put(String key, byte[] content, String contentType) {
        s3.putObject(request -> request.bucket(bucket).key(key).contentType(contentType),
                RequestBody.fromBytes(content));
    }

    @Override
    public Optional<StoredObject> get(String key) {
        try {
            var object = s3.getObjectAsBytes(request -> request.bucket(bucket).key(key));
            return Optional.of(new StoredObject(object.asByteArray(), object.response().contentType()));
        } catch (NoSuchKeyException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        s3.deleteObject(request -> request.bucket(bucket).key(key));
    }
}
