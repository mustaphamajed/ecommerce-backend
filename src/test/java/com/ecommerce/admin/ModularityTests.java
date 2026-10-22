package com.ecommerce.admin;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(AdminBackendApplication.class);

    @Test
    void modulesOnlyUseEachOthersPublicApi() {
        modules.verify();
    }
}
