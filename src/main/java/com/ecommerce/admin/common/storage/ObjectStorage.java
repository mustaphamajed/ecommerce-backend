package com.ecommerce.admin.common.storage;

import java.util.Optional;

public interface ObjectStorage {

    void put(String key, byte[] content, String contentType);

    Optional<StoredObject> get(String key);

    void delete(String key);
}
