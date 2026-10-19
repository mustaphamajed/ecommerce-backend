package com.ecommerce.admin.common;

import java.util.Locale;

public final class SearchPatterns {

    private SearchPatterns() {
    }

    public static String contains(String term) {
        if (term == null || term.isBlank()) {
            return "%";
        }
        String escaped = term.trim().toLowerCase(Locale.ROOT)
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escaped + "%";
    }
}
