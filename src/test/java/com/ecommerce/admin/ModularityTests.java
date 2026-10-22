package com.ecommerce.admin;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    private final ApplicationModules modules =
            ApplicationModules.of(AdminBackendApplication.class, resideInAPackage("com.ecommerce.admin.demo.."));

    @Test
    void modulesOnlyUseEachOthersPublicApi() {
        modules.verify();
    }
}
