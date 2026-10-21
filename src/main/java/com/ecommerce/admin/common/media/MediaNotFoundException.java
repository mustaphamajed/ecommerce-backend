package com.ecommerce.admin.common.media;

import com.ecommerce.admin.common.exception.NotFoundException;

public class MediaNotFoundException extends NotFoundException {

    public MediaNotFoundException(String key) {
        super("MEDIA_NOT_FOUND", "No file found at " + key);
    }
}
