package com.ecommerce.admin.demo;

import static com.ecommerce.admin.demo.DemoProduct.active;
import static com.ecommerce.admin.demo.DemoProduct.archived;

import java.util.List;

final class DemoCatalog {

    static final String RUNNING_SHOES = "Running shoes";
    static final String T_SHIRTS = "T-shirts";
    static final String HOODIES = "Hoodies & sweatshirts";
    static final String BAGS = "Bags";
    static final String ACCESSORIES = "Accessories";
    static final String ELECTRONICS = "Sport electronics";
    static final String YOGA = "Yoga & fitness";
    static final String SWIMWEAR = "Swimwear";
    static final String OUTDOOR = "Outdoor & camping";
    static final String KIDS = "Kids";

    static final List<DemoCategory> CATEGORIES = List.of(
            new DemoCategory(RUNNING_SHOES, "Road and trail running shoes for every distance."),
            new DemoCategory(T_SHIRTS, "Everyday and sport t-shirts in soft cotton and technical fabrics."),
            new DemoCategory(HOODIES, "Warm layers for cool mornings and evenings."),
            new DemoCategory(BAGS, "Backpacks and bags for the gym, work and travel."),
            new DemoCategory(ACCESSORIES, "Caps, socks, bottles and other essentials."),
            new DemoCategory(ELECTRONICS, "Watches, earbuds and trackers to follow your training."),
            new DemoCategory(YOGA, "Mats, bands and equipment for home and studio workouts."),
            new DemoCategory(SWIMWEAR, "Swimsuits, goggles and caps for the pool and open water."),
            new DemoCategory(OUTDOOR, "Tents, sleeping bags and gear for nights outside."),
            new DemoCategory(KIDS, "Sport clothing and shoes for young athletes."));

    static final List<DemoProduct> PRODUCTS = List.of(
            active("RUN-ROAD-001", "Road Runner 2", "Lightweight road shoe with responsive cushioning.", "89.90", RUNNING_SHOES),
            active("RUN-ROAD-002", "Tempo Pro", "Carbon-plated shoe built for race day.", "179.00", RUNNING_SHOES),
            active("RUN-TRAIL-001", "Trail Grip X", "Aggressive outsole for muddy and rocky trails.", "129.50", RUNNING_SHOES),
            active("RUN-DAILY-001", "Daily Trainer", "Comfortable everyday trainer for easy miles.", "74.99", RUNNING_SHOES),
            archived("RUN-ROAD-000", "Road Runner 1", "Previous generation of the Road Runner.", "59.90", RUNNING_SHOES),

            active("TEE-BASIC-WHT", "Essential Tee - White", "Organic cotton crew-neck t-shirt.", "19.90", T_SHIRTS),
            active("TEE-BASIC-BLK", "Essential Tee - Black", "Organic cotton crew-neck t-shirt.", "19.90", T_SHIRTS),
            active("TEE-TECH-001", "Dry-Fit Training Tee", "Breathable, quick-dry fabric for workouts.", "29.00", T_SHIRTS),
            active("TEE-LONG-001", "Long Sleeve Run Tee", "Light long-sleeve layer with reflective details.", "34.50", T_SHIRTS),

            active("HOOD-ZIP-GRY", "Zip Hoodie - Grey", "Brushed fleece hoodie with a full zip.", "59.00", HOODIES),
            active("HOOD-PULL-NVY", "Pullover Hoodie - Navy", "Classic pullover with kangaroo pocket.", "54.90", HOODIES),
            active("SWEAT-CREW-001", "Crew Sweatshirt", "Relaxed fit crew-neck sweatshirt.", "44.90", HOODIES),
            archived("HOOD-ZIP-RED", "Zip Hoodie - Red", "Limited edition colour, no longer produced.", "49.00", HOODIES),

            active("BAG-BACK-20L", "Daypack 20L", "Compact backpack with laptop sleeve.", "49.90", BAGS),
            active("BAG-GYM-001", "Gym Duffel", "Spacious duffel with a separate shoe compartment.", "39.90", BAGS),
            active("BAG-TRAVEL-35L", "Travel Backpack 35L", "Carry-on size backpack with clamshell opening.", "99.00", BAGS),

            active("ACC-CAP-001", "Running Cap", "Lightweight cap with sweat band.", "17.50", ACCESSORIES),
            active("ACC-SOCK-3PK", "Running Socks (3 pack)", "Cushioned socks with arch support.", "14.90", ACCESSORIES),
            active("ACC-BOTTLE-750", "Insulated Bottle 750ml", "Keeps drinks cold for 24 hours.", "24.90", ACCESSORIES),
            active("ACC-BELT-001", "Running Belt", "Slim belt for phone, keys and gels.", "22.00", ACCESSORIES),
            active("ACC-HEAD-001", "Thermal Headband", "Warm headband for winter runs.", "12.90", ACCESSORIES),

            active("ELEC-WATCH-GPS", "GPS Sport Watch", "Tracks pace, distance and heart rate for 20 hours.", "199.00", ELECTRONICS),
            active("ELEC-BUDS-001", "Wireless Sport Earbuds", "Sweat-resistant earbuds with secure ear hooks.", "79.90", ELECTRONICS),
            active("ELEC-HRM-001", "Chest Heart Rate Monitor", "Accurate heart rate strap with Bluetooth.", "54.00", ELECTRONICS),
            archived("ELEC-WATCH-OLD", "Basic Sport Watch", "Replaced by the GPS Sport Watch.", "69.00", ELECTRONICS),

            active("YOGA-MAT-6MM", "Yoga Mat 6mm", "Non-slip mat with alignment lines.", "34.90", YOGA),
            active("YOGA-BLOCK-2PK", "Yoga Blocks (2 pack)", "Lightweight foam blocks for support.", "16.90", YOGA),
            active("FIT-BANDS-SET", "Resistance Bands Set", "Five bands from light to extra heavy.", "24.50", YOGA),
            active("FIT-KETTLE-12", "Kettlebell 12kg", "Cast iron kettlebell with a wide handle.", "42.00", YOGA),

            active("SWIM-SUIT-W01", "Women's Training Swimsuit", "Chlorine-resistant fabric for daily training.", "39.90", SWIMWEAR),
            active("SWIM-SHORT-M01", "Men's Swim Shorts", "Quick-dry shorts with inner mesh.", "29.90", SWIMWEAR),
            active("SWIM-GOGG-001", "Anti-Fog Goggles", "Wide view goggles with UV protection.", "15.90", SWIMWEAR),
            active("SWIM-CAP-001", "Silicone Swim Cap", "Durable cap that keeps hair dry.", "9.90", SWIMWEAR),

            active("OUT-TENT-2P", "Trekking Tent 2P", "Lightweight two-person tent, sets up in 5 minutes.", "149.00", OUTDOOR),
            active("OUT-SLEEP-5C", "Sleeping Bag 5°C", "Compact sleeping bag for summer nights.", "69.90", OUTDOOR),
            active("OUT-LAMP-001", "Rechargeable Headlamp", "300 lumens with red light mode.", "27.90", OUTDOOR),
            active("OUT-STOVE-001", "Camping Stove", "Foldable gas stove with piezo ignition.", "34.00", OUTDOOR),

            active("KIDS-SHOE-001", "Kids Running Shoes", "Easy hook-and-loop closure and soft cushioning.", "39.90", KIDS),
            active("KIDS-TEE-001", "Kids Sport Tee", "Breathable tee for school sports.", "12.90", KIDS),
            active("KIDS-JACKET-001", "Kids Rain Jacket", "Waterproof jacket with a packable hood.", "34.90", KIDS));

    private DemoCatalog() {
    }
}
