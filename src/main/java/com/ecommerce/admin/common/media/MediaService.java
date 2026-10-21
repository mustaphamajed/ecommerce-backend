package com.ecommerce.admin.common.media;

import com.ecommerce.admin.common.storage.ObjectStorage;
import com.ecommerce.admin.common.storage.StoredObject;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class MediaService {

    public static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024;
    public static final String URL_PREFIX = "/api/media/";

    private static final Logger log = LoggerFactory.getLogger(MediaService.class);
    private static final Pattern SAFE_KEY = Pattern.compile("^[a-z0-9-]+(/[A-Za-z0-9-]+)*/[0-9a-f-]{36}\\.(jpg|png|webp)$");

    private final ObjectStorage storage;

    public MediaService(ObjectStorage storage) {
        this.storage = storage;
    }

    public String uploadImage(String folder, byte[] content) {
        var type = validateImage(content);
        String key = folder + "/" + UUID.randomUUID() + "." + type.extension();
        storage.put(key, content, type.contentType());
        runOnRollback(() -> deleteQuietly(key));
        return key;
    }

    public void deleteAfterCommit(String key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    deleteQuietly(key);
                }
            });
        } else {
            deleteQuietly(key);
        }
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

    private static ImageType validateImage(byte[] content) {
        if (content.length == 0) {
            throw InvalidImageException.empty();
        }
        if (content.length > MAX_IMAGE_SIZE_BYTES) {
            throw InvalidImageException.tooLarge(MAX_IMAGE_SIZE_BYTES);
        }
        return ImageType.detect(content).orElseThrow(InvalidImageException::unsupportedType);
    }

    private void runOnRollback(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        action.run();
                    }
                }
            });
        }
    }

    private void deleteQuietly(String key) {
        try {
            storage.delete(key);
        } catch (RuntimeException e) {
            log.warn("Could not delete stored file {}: {}", key, e.getMessage());
        }
    }
}
