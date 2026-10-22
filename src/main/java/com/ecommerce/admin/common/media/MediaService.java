package com.ecommerce.admin.common.media;

import com.ecommerce.admin.common.storage.ObjectStorage;
import com.ecommerce.admin.common.storage.StoredObject;
import com.ecommerce.admin.common.transaction.TransactionCallbacks;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

    public static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024;
    public static final String URL_PREFIX = "/api/media/";

    private static final Logger log = LoggerFactory.getLogger(MediaService.class);
    private static final Pattern SAFE_KEY = Pattern.compile("^[a-z0-9-]+(/[A-Za-z0-9-]+)*/[0-9a-f-]{36}\\.(jpg|png|webp)$");

    private final ObjectStorage storage;
    private final MediaMetrics metrics;

    public MediaService(ObjectStorage storage, MediaMetrics metrics) {
        this.storage = storage;
        this.metrics = metrics;
    }

    public String uploadImage(String folder, byte[] content) {
        var type = validateImage(folder, content);
        String key = folder + "/" + UUID.randomUUID() + "." + type.extension();
        metrics.timeUpload(folder, type, () -> storage.put(key, content, type.contentType()));
        TransactionCallbacks.afterRollback(() -> deleteQuietly(key));
        return key;
    }

    public void deleteAfterCommit(String key) {
        TransactionCallbacks.afterCommit(() -> deleteQuietly(key));
    }

    public StoredObject load(String key) {
        if (!SAFE_KEY.matcher(key).matches()) {
            throw new MediaNotFoundException(key);
        }
        return storage.get(key).orElseThrow(() -> new MediaNotFoundException(key));
    }

    public static String urlOf(String key) {
        return URL_PREFIX + key;
    }

    public static Optional<String> urlOf(Optional<String> key) {
        return key.map(MediaService::urlOf);
    }

    private ImageType validateImage(String folder, byte[] content) {
        if (content.length == 0) {
            metrics.uploadRejected(folder, "empty");
            throw InvalidImageException.empty();
        }
        if (content.length > MAX_IMAGE_SIZE_BYTES) {
            metrics.uploadRejected(folder, "too_large");
            throw InvalidImageException.tooLarge(MAX_IMAGE_SIZE_BYTES);
        }
        return ImageType.detect(content).orElseThrow(() -> {
            metrics.uploadRejected(folder, "unsupported_type");
            return InvalidImageException.unsupportedType();
        });
    }

    private void deleteQuietly(String key) {
        try {
            storage.delete(key);
        } catch (RuntimeException e) {
            log.warn("Could not delete stored file {}: {}", key, e.getMessage());
        }
    }
}
