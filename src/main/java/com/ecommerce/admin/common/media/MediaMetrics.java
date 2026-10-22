package com.ecommerce.admin.common.media;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class MediaMetrics {

    private final MeterRegistry registry;

    public MediaMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void timeUpload(String folder, ImageType type, Runnable upload) {
        Timer.builder("media.uploads")
                .description("Time to store an uploaded image")
                .tag("folder", rootOf(folder))
                .tag("type", type.name().toLowerCase())
                .register(registry)
                .record(upload);
    }

    public void uploadRejected(String folder, String reason) {
        Counter.builder("media.uploads.rejected")
                .description("Uploads rejected by validation")
                .tag("folder", rootOf(folder))
                .tag("reason", reason)
                .register(registry)
                .increment();
    }

    private static String rootOf(String folder) {
        int slash = folder.indexOf('/');
        return slash < 0 ? folder : folder.substring(0, slash);
    }
}
