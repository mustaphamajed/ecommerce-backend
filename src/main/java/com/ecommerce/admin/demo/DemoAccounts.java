package com.ecommerce.admin.demo;

import java.util.List;

final class DemoAccounts {

    static final String ADMIN = "ADMIN";
    static final String STOCK_MANAGER = "STOCK_MANAGER";
    static final String VIEWER = "VIEWER";

    static final List<DemoWarehouse> WAREHOUSES = List.of(
            new DemoWarehouse("MAIN", "Main warehouse"),
            new DemoWarehouse("PAR", "Paris - Saint-Ouen"),
            new DemoWarehouse("LYO", "Lyon - Vénissieux"),
            new DemoWarehouse("MRS", "Marseille - Vitrolles"),
            new DemoWarehouse("LIL", "Lille - Lesquin"));

    static final List<DemoUser> USERS = List.of(
            new DemoUser("sara.admin@shop.local", "Sara", "Benali", List.of(ADMIN), List.of(), true),
            new DemoUser("karim.stock@shop.local", "Karim", "Haddad", List.of(STOCK_MANAGER), List.of("PAR", "LIL"), true),
            new DemoUser("lina.stock@shop.local", "Lina", "Moreau", List.of(STOCK_MANAGER), List.of("LYO", "MRS"), true),
            new DemoUser("hugo.stock@shop.local", "Hugo", "Martin", List.of(STOCK_MANAGER), List.of("MAIN"), true),
            new DemoUser("ines.viewer@shop.local", "Inès", "Dubois", List.of(VIEWER), List.of(), true),
            new DemoUser("tom.viewer@shop.local", "Tom", "Leroy", List.of(VIEWER), List.of(), true),
            new DemoUser("nadia.lead@shop.local", "Nadia", "Petit", List.of(STOCK_MANAGER, VIEWER), List.of("PAR", "LYO", "MRS", "LIL"), true),
            new DemoUser("paul.former@shop.local", "Paul", "Girard", List.of(STOCK_MANAGER), List.of("PAR"), false));

    private DemoAccounts() {
    }
}
