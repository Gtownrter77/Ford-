package com.example.data

import com.example.model.CompetitorPrice
import com.example.model.OreillyCommercialAccount
import com.example.model.PartItem

object SportTracPartsCatalog {

    val defaultCommercialAccount = OreillyCommercialAccount()

    // Comprehensive catalog of 2004 Ford Explorer Sport Trac parts with O'Reilly Commercial Pricing vs Competitors
    val catalog: List<PartItem> = listOf(
        // Spark Plugs & Ignition
        PartItem(
            id = "part_spark_plug_motorcraft",
            componentId = "spark_plugs_40l",
            partName = "Motorcraft Platinum Spark Plug (Set of 6)",
            brand = "Motorcraft",
            partNumber = "SP-498 / AWSF-32PM",
            oreillyCommercialPrice = 23.94, // $3.99 ea
            oreillyRetailPrice = 35.94,
            inStockLocalStore = true,
            storeStockCount = 18,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 24.50, 0.0, "Free 2-Day Shipping"),
                CompetitorPrice("RockAuto", 22.80, 5.99, "Ships in 2 Days"),
                CompetitorPrice("eBay Motors", 23.10, 0.0, "Top Rated Seller"),
                CompetitorPrice("AutoZone", 32.99, 0.0, "Retail Counter"),
                CompetitorPrice("Advance Auto Parts", 34.49, 0.0, "In Store"),
                CompetitorPrice("NAPA Auto Parts", 33.99, 0.0, "Local Store Pickup"),
                CompetitorPrice("Summit Racing", 25.99, 6.95, "In Stock")
            ),
            category = "Ignition",
            warranty = "2 Year / Unlimited Mile Warranty"
        ),
        PartItem(
            id = "part_spark_plug_bosch",
            componentId = "spark_plugs_40l",
            partName = "Bosch Double Platinum Spark Plug (Set of 6)",
            brand = "BOSCH",
            partNumber = "8102",
            oreillyCommercialPrice = 28.50,
            oreillyRetailPrice = 41.94,
            inStockLocalStore = true,
            storeStockCount = 12,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 29.99, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 27.12, 5.99, "Wholesale Price"),
                CompetitorPrice("eBay Motors", 28.15, 0.0, "Authorized Distributor"),
                CompetitorPrice("AutoZone", 38.99, 0.0, "In Store"),
                CompetitorPrice("Advance Auto Parts", 39.99, 0.0, "Retail Counter"),
                CompetitorPrice("NAPA Auto Parts", 37.99, 0.0, "Local Store"),
                CompetitorPrice("Summit Racing", 31.50, 6.95)
            ),
            category = "Ignition",
            warranty = "3 Year Manufacturer Warranty"
        ),

        // Oil Filter & Maintenance
        PartItem(
            id = "part_oil_filter_motorcraft",
            componentId = "oil_filter_housing",
            partName = "Motorcraft Engine Oil Filter FL-820S",
            brand = "Motorcraft",
            partNumber = "FL-820S",
            oreillyCommercialPrice = 4.89,
            oreillyRetailPrice = 7.99,
            inStockLocalStore = true,
            storeStockCount = 24,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 5.25, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 4.12, 4.99),
                CompetitorPrice("eBay Motors", 4.95, 0.0, "Free Shipping"),
                CompetitorPrice("AutoZone", 7.49, 0.0, "In Store Pickup"),
                CompetitorPrice("Advance Auto Parts", 7.99, 0.0, "In Store"),
                CompetitorPrice("NAPA Auto Parts", 7.29, 0.0, "In Store Pickup")
            ),
            category = "Filters",
            warranty = "1 Year Warranty"
        ),
        PartItem(
            id = "part_oil_wix",
            componentId = "oil_filter_housing",
            partName = "WIX Spin-On Oil Filter Heavy Duty",
            brand = "WIX",
            partNumber = "51372",
            oreillyCommercialPrice = 7.25,
            oreillyRetailPrice = 11.49,
            inStockLocalStore = true,
            storeStockCount = 14,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 7.99, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 6.88, 4.99),
                CompetitorPrice("eBay Motors", 7.50, 0.0, "Free Shipping"),
                CompetitorPrice("AutoZone", 10.99, 0.0, "In Store Pickup"),
                CompetitorPrice("Advance Auto Parts", 11.49, 0.0, "In Store"),
                CompetitorPrice("NAPA Auto Parts", 9.99, 0.0, "Gold Filter Equivalent")
            ),
            category = "Filters",
            warranty = "1 Year Warranty"
        ),

        // Thermostat Housing Assembly
        PartItem(
            id = "part_thermostat_housing_dorman",
            componentId = "thermostat_housing",
            partName = "Dorman Complete Aluminum Thermostat Housing Kit",
            brand = "Dorman OE Solutions",
            partNumber = "902-861",
            oreillyCommercialPrice = 48.99,
            oreillyRetailPrice = 72.99,
            inStockLocalStore = true,
            storeStockCount = 4,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 52.99, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 46.79, 7.99, "Wholesale Price"),
                CompetitorPrice("eBay Motors", 49.50, 0.0, "Direct OEM Seller"),
                CompetitorPrice("AutoZone", 69.99, 0.0, "In Store Counter"),
                CompetitorPrice("Advance Auto Parts", 74.99, 0.0, "In Store Pickup"),
                CompetitorPrice("Summit Racing", 54.99, 6.95, "Warehouse Direct")
            ),
            category = "Cooling System",
            warranty = "Limited Lifetime Warranty"
        ),

        // Serpentine Belt
        PartItem(
            id = "part_serpentine_belt_gates",
            componentId = "serpentine_belt",
            partName = "Gates Micro-V Serpentine Belt",
            brand = "Gates",
            partNumber = "K060868",
            oreillyCommercialPrice = 21.49,
            oreillyRetailPrice = 33.99,
            inStockLocalStore = true,
            storeStockCount = 9,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 22.99, 0.0, "Free 2-Day Shipping"),
                CompetitorPrice("RockAuto", 19.82, 5.99),
                CompetitorPrice("eBay Motors", 21.80, 0.0, "Authorized Dealer"),
                CompetitorPrice("AutoZone", 31.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 33.99, 0.0),
                CompetitorPrice("NAPA Auto Parts", 29.99, 0.0)
            ),
            category = "Belts & Hoses",
            warranty = "Lifetime Warranty"
        ),

        // Alternator
        PartItem(
            id = "part_alternator_ultrapower",
            componentId = "battery_alternator",
            partName = "Ultrapower 130Amp High Output Alternator",
            brand = "Ultrapower",
            partNumber = "13883",
            oreillyCommercialPrice = 129.99,
            oreillyRetailPrice = 189.99,
            coreDeposit = 25.00,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 135.00, 0.0, "Prime 2-Day"),
                CompetitorPrice("RockAuto", 118.79, 12.99),
                CompetitorPrice("eBay Motors", 124.95, 0.0, "New In Box"),
                CompetitorPrice("AutoZone", 179.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 184.99, 0.0),
                CompetitorPrice("Summit Racing", 139.99, 0.0)
            ),
            category = "Charging System",
            warranty = "Lifetime Replacement Warranty"
        ),

        // Front Brake Pads & Rotors
        PartItem(
            id = "part_brake_pads_ceramic",
            componentId = "front_brakes",
            partName = "BrakeBest Select Ceramic Front Brake Pads",
            brand = "BrakeBest",
            partNumber = "MKD833",
            oreillyCommercialPrice = 28.99,
            oreillyRetailPrice = 42.99,
            inStockLocalStore = true,
            storeStockCount = 11,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 31.50, 0.0, "Free Delivery"),
                CompetitorPrice("RockAuto", 24.50, 6.99),
                CompetitorPrice("eBay Motors", 27.99, 0.0, "Free Fast Shipping"),
                CompetitorPrice("AutoZone", 39.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 41.99, 0.0)
            ),
            category = "Brakes",
            warranty = "Limited Lifetime Warranty"
        ),
        PartItem(
            id = "part_front_rotor_vented",
            componentId = "front_brakes",
            partName = "BrakeBest Select Front Vented Brake Rotor (Single)",
            brand = "BrakeBest",
            partNumber = "54093",
            oreillyCommercialPrice = 41.50,
            oreillyRetailPrice = 62.99,
            inStockLocalStore = true,
            storeStockCount = 8,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 44.99, 0.0, "Prime Heavy Ship"),
                CompetitorPrice("RockAuto", 38.79, 9.99),
                CompetitorPrice("eBay Motors", 42.00, 0.0),
                CompetitorPrice("AutoZone", 59.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 62.99, 0.0)
            ),
            category = "Brakes",
            warranty = "2 Year Warranty"
        ),

        // 4x4 Shift Motor
        PartItem(
            id = "part_4x4_shift_motor",
            componentId = "driveshaft_4x4",
            partName = "Dorman 4x4 Electric Transfer Case Shift Motor",
            brand = "Dorman",
            partNumber = "600-802",
            oreillyCommercialPrice = 94.50,
            oreillyRetailPrice = 139.99,
            inStockLocalStore = true,
            storeStockCount = 2,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 98.99, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 88.79, 8.99),
                CompetitorPrice("eBay Motors", 91.50, 0.0, "Top Rated Plus"),
                CompetitorPrice("AutoZone", 134.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 139.99, 0.0),
                CompetitorPrice("Summit Racing", 99.99, 6.95)
            ),
            category = "4WD & Drivetrain",
            warranty = "Limited Lifetime Warranty"
        ),

        // Upper Intake Manifold Gasket
        PartItem(
            id = "part_intake_gasket_felpro",
            componentId = "upper_intake_manifold",
            partName = "Fel-Pro Upper Intake Manifold Gasket Set",
            brand = "Fel-Pro",
            partNumber = "MS 96324",
            oreillyCommercialPrice = 14.89,
            oreillyRetailPrice = 22.99,
            inStockLocalStore = true,
            storeStockCount = 7,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 15.99, 0.0),
                CompetitorPrice("RockAuto", 12.90, 4.99),
                CompetitorPrice("eBay Motors", 14.20, 0.0),
                CompetitorPrice("AutoZone", 21.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 22.99, 0.0)
            ),
            category = "Gaskets",
            warranty = "1 Year Warranty"
        ),

        // Hydraulic Timing Tensioner
        PartItem(
            id = "part_timing_tensioner_cloyes",
            componentId = "timing_cassette_front",
            partName = "Cloyes Hydraulic Timing Chain Tensioner (Front)",
            brand = "Cloyes",
            partNumber = "9-5431",
            oreillyCommercialPrice = 36.99,
            oreillyRetailPrice = 54.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 38.50, 0.0),
                CompetitorPrice("RockAuto", 31.79, 5.99),
                CompetitorPrice("eBay Motors", 34.99, 0.0),
                CompetitorPrice("AutoZone", 52.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 54.99, 0.0),
                CompetitorPrice("Summit Racing", 39.99, 6.95)
            ),
            category = "Engine Timing",
            warranty = "1 Year Warranty"
        ),

        // Automatic Transmission Filter & Pan Gasket
        PartItem(
            id = "part_trans_filter_wix",
            componentId = "transmission_5r55e",
            partName = "WIX 5R55E Transmission Filter & Pan Gasket Kit",
            brand = "WIX",
            partNumber = "58841",
            oreillyCommercialPrice = 18.25,
            oreillyRetailPrice = 27.99,
            inStockLocalStore = true,
            storeStockCount = 6,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 19.50, 0.0),
                CompetitorPrice("RockAuto", 15.80, 5.99),
                CompetitorPrice("eBay Motors", 17.90, 0.0),
                CompetitorPrice("AutoZone", 25.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 27.99, 0.0)
            ),
            category = "Transmission",
            warranty = "1 Year Warranty"
        ),

        // 9007 Headlight Bulbs
        PartItem(
            id = "part_headlight_bulbs_motorcraft",
            componentId = "wiring_lighting_3d",
            partName = "Motorcraft 9007 Dual Beam XtraVision Headlight Bulbs (Pair)",
            brand = "Motorcraft",
            partNumber = "9007-XV2",
            oreillyCommercialPrice = 22.50,
            oreillyRetailPrice = 31.99,
            inStockLocalStore = true,
            storeStockCount = 15,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 23.99, 0.0, "Prime 2-Day"),
                CompetitorPrice("RockAuto", 19.45, 4.99, "Ships Today"),
                CompetitorPrice("eBay Motors", 21.50, 0.0, "Free Fast Shipping"),
                CompetitorPrice("AutoZone", 32.99, 0.0, "In Store Pickup"),
                CompetitorPrice("Advance Auto Parts", 33.49, 0.0)
            ),
            category = "Lighting",
            warranty = "1 Year Warranty"
        ),

        // Headlight Housing Assembly Set
        PartItem(
            id = "part_headlight_assembly_anzo",
            componentId = "wiring_lighting_3d",
            partName = "Anzo USA OEM Style Crystal Clear Headlight & Corner Lens Set",
            brand = "Anzo USA",
            partNumber = "111042",
            oreillyCommercialPrice = 98.00,
            oreillyRetailPrice = 139.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 109.99, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 94.75, 11.99, "Wholesale Warehouse"),
                CompetitorPrice("eBay Motors", 99.00, 0.0, "Top Rated Seller"),
                CompetitorPrice("AutoZone", 149.99, 0.0),
                CompetitorPrice("Summit Racing", 112.50, 6.95)
            ),
            category = "Lighting",
            warranty = "1 Year Limited Warranty"
        ),

        // Wiring Harness Pigtail Repair Kit
        PartItem(
            id = "part_wiring_harness_dorman",
            componentId = "wiring_lighting_3d",
            partName = "Dorman Conduct-Tite Heavy Duty Headlight Wiring Socket Harness Pigtail",
            brand = "Dorman",
            partNumber = "84790",
            oreillyCommercialPrice = 11.20,
            oreillyRetailPrice = 16.99,
            inStockLocalStore = true,
            storeStockCount = 8,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 12.50, 0.0, "Free Delivery"),
                CompetitorPrice("RockAuto", 9.80, 4.99),
                CompetitorPrice("eBay Motors", 11.00, 0.0),
                CompetitorPrice("AutoZone", 17.49, 0.0),
                CompetitorPrice("NAPA Auto Parts", 16.99, 0.0)
            ),
            category = "Wiring & Electrical",
            warranty = "Lifetime Warranty"
        ),

        // Brake Master Cylinder Assembly
        PartItem(
            id = "part_brake_master_cylinder_dorman",
            componentId = "abs_master_cylinder_3d",
            partName = "Dorman Brake Master Cylinder Assembly with Reservoir",
            brand = "Dorman OE Solutions",
            partNumber = "M630041",
            oreillyCommercialPrice = 52.50,
            oreillyRetailPrice = 78.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 56.99, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 49.80, 8.99),
                CompetitorPrice("eBay Motors", 53.00, 0.0),
                CompetitorPrice("AutoZone", 76.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 79.99, 0.0),
                CompetitorPrice("NAPA Auto Parts", 74.50, 0.0)
            ),
            category = "Brakes",
            warranty = "Limited Lifetime Warranty"
        ),

        // Rear Brake Pads & Rotors Kit
        PartItem(
            id = "part_rear_brake_pads_rotors_bosch",
            componentId = "rear_brakes_3d",
            partName = "Bosch QuietCast Rear Premium Disc Brake Rotor & Ceramic Pad Kit",
            brand = "Bosch",
            partNumber = "BC881 / 15010041",
            oreillyCommercialPrice = 68.99,
            oreillyRetailPrice = 99.99,
            inStockLocalStore = true,
            storeStockCount = 5,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 72.50, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 64.20, 11.99),
                CompetitorPrice("eBay Motors", 67.80, 0.0),
                CompetitorPrice("AutoZone", 96.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 99.99, 0.0)
            ),
            category = "Brakes",
            warranty = "2 Year Warranty"
        ),

        // Walker Stainless Steel Cat-Back Muffler Assembly
        PartItem(
            id = "part_catback_muffler_walker",
            componentId = "catback_exhaust_muffler_3d",
            partName = "Walker Quiet-Flow Stainless Steel Exhaust Muffler & Assembly",
            brand = "Walker Exhaust",
            partNumber = "54371",
            oreillyCommercialPrice = 118.00,
            oreillyRetailPrice = 169.99,
            inStockLocalStore = true,
            storeStockCount = 2,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 124.99, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 112.50, 14.99, "Wholesale Warehouse"),
                CompetitorPrice("eBay Motors", 119.00, 0.0),
                CompetitorPrice("AutoZone", 164.99, 0.0),
                CompetitorPrice("Summit Racing", 129.99, 6.95)
            ),
            category = "Exhaust",
            warranty = "Limited Lifetime Warranty"
        ),

        // Oxygen Sensor (HO2S)
        PartItem(
            id = "part_o2_sensor_bosch",
            componentId = "oxygen_sensors_3d",
            partName = "Bosch Direct Fit Heated Oxygen Sensor (HO2S)",
            brand = "Bosch",
            partNumber = "15717",
            oreillyCommercialPrice = 31.50,
            oreillyRetailPrice = 46.99,
            inStockLocalStore = true,
            storeStockCount = 8,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 32.99, 0.0),
                CompetitorPrice("RockAuto", 28.70, 5.99),
                CompetitorPrice("eBay Motors", 30.50, 0.0),
                CompetitorPrice("AutoZone", 44.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 46.99, 0.0)
            ),
            category = "Exhaust",
            warranty = "1 Year Warranty"
        ),

        // All-Terrain Tires
        PartItem(
            id = "part_tire_goodyear_adventure",
            componentId = "tires_wheels_3d",
            partName = "Goodyear Wrangler All-Terrain Adventure Tire (265/70R16 112T)",
            brand = "Goodyear",
            partNumber = "756059571",
            oreillyCommercialPrice = 142.00,
            oreillyRetailPrice = 189.99,
            inStockLocalStore = true,
            storeStockCount = 8,
            competitorPrices = listOf(
                CompetitorPrice("Discount Tire", 184.99, 0.0, "Free Mounting & Balance"),
                CompetitorPrice("Tire Rack", 178.50, 15.00),
                CompetitorPrice("Amazon Prime", 182.00, 0.0, "Prime Delivery"),
                CompetitorPrice("Walmart", 179.00, 0.0),
                CompetitorPrice("AutoZone", 189.99, 0.0)
            ),
            category = "Tires & Wheels",
            warranty = "60,000 Mile Tread Life Limited Warranty"
        ),

        // 16-inch Aluminum Wheel
        PartItem(
            id = "part_wheel_oe_aluminum",
            componentId = "tires_wheels_3d",
            partName = "OE Reconditioned 16x7.0 5-Spoke Machined Cast Aluminum Wheel",
            brand = "Dorman OE Solutions",
            partNumber = "939-105",
            oreillyCommercialPrice = 115.00,
            oreillyRetailPrice = 159.99,
            inStockLocalStore = true,
            storeStockCount = 4,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 149.99, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 128.50, 12.99),
                CompetitorPrice("eBay Motors", 135.00, 0.0),
                CompetitorPrice("AutoZone", 159.99, 0.0)
            ),
            category = "Tires & Wheels",
            warranty = "1 Year Finish & Structural Warranty"
        ),

        // Wheel Hub & Bearing Assembly
        PartItem(
            id = "part_hub_assembly_moog",
            componentId = "wheel_bearings_hubs_3d",
            partName = "Moog Front Wheel Hub & Sealed Bearing Assembly with ABS Harness",
            brand = "Moog Chassis Parts",
            partNumber = "515052",
            oreillyCommercialPrice = 88.50,
            oreillyRetailPrice = 129.99,
            inStockLocalStore = true,
            storeStockCount = 4,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 98.99, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 82.20, 8.99),
                CompetitorPrice("eBay Motors", 89.00, 0.0),
                CompetitorPrice("AutoZone", 124.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 129.99, 0.0)
            ),
            category = "Tires & Wheels",
            warranty = "3 Year / 36,000 Mile Warranty"
        ),

        // Chrome Wheel Lug Nuts 20-Pack
        PartItem(
            id = "part_lug_nuts_dorman",
            componentId = "tires_wheels_3d",
            partName = "Dorman Heavy Duty Chrome Wheel Lug Nut Set (1/2-in-20 Thread, 20 Pack)",
            brand = "Dorman",
            partNumber = "611-197.1",
            oreillyCommercialPrice = 18.50,
            oreillyRetailPrice = 28.99,
            inStockLocalStore = true,
            storeStockCount = 6,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 22.99, 0.0),
                CompetitorPrice("RockAuto", 16.80, 5.99),
                CompetitorPrice("AutoZone", 27.99, 0.0)
            ),
            category = "Tires & Wheels",
            warranty = "1 Year Warranty"
        ),

        // Instrument Gauge Cluster Assembly
        PartItem(
            id = "part_instrument_cluster_dorman",
            componentId = "dash_dashboard_cluster_3d",
            partName = "Dorman OE Solutions Reconditioned Instrument Cluster (White Face Gauges)",
            brand = "Dorman OE Solutions",
            partNumber = "599-312",
            oreillyCommercialPrice = 145.00,
            oreillyRetailPrice = 199.99,
            inStockLocalStore = true,
            storeStockCount = 2,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 168.99, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 138.50, 10.99),
                CompetitorPrice("eBay Motors", 142.00, 0.0),
                CompetitorPrice("AutoZone", 195.99, 0.0)
            ),
            category = "Electrical & Dash",
            warranty = "Limited Lifetime Warranty"
        ),

        // Gauge Backlight LED Bulb Conversion Kit
        PartItem(
            id = "part_cluster_led_bulbs_sylvania",
            componentId = "dash_dashboard_cluster_3d",
            partName = "Sylvania ZEVO White LED Mini-Bulb 6-Pack (Dash Gauge Backlight)",
            brand = "Sylvania",
            partNumber = "194LED.BP2",
            oreillyCommercialPrice = 12.50,
            oreillyRetailPrice = 18.99,
            inStockLocalStore = true,
            storeStockCount = 10,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 14.99, 0.0),
                CompetitorPrice("AutoZone", 18.99, 0.0)
            ),
            category = "Electrical & Dash",
            warranty = "1 Year Warranty"
        ),

        // Central Junction Box (Inside Fuse Block)
        PartItem(
            id = "part_dash_fuse_block_dorman",
            componentId = "dash_wiring_harness_3d",
            partName = "Dorman Central Junction Box Interior Fuse Block & Relay Module",
            brand = "Dorman OE Solutions",
            partNumber = "601-028",
            oreillyCommercialPrice = 82.00,
            oreillyRetailPrice = 119.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 94.50, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 76.80, 8.99),
                CompetitorPrice("AutoZone", 114.99, 0.0)
            ),
            category = "Electrical & Dash",
            warranty = "1 Year Warranty"
        ),

        // Dash Wiring Repair Connector & Terminals
        PartItem(
            id = "part_dash_wiring_repair_pigtails",
            componentId = "dash_wiring_harness_3d",
            partName = "Pico Automotive Multi-Pin Under-Dash Wiring Harness Repair Kit",
            brand = "Pico",
            partNumber = "5629 PT",
            oreillyCommercialPrice = 14.20,
            oreillyRetailPrice = 22.99,
            inStockLocalStore = true,
            storeStockCount = 7,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 17.99, 0.0),
                CompetitorPrice("AutoZone", 21.99, 0.0)
            ),
            category = "Electrical & Dash",
            warranty = "90 Day Warranty"
        ),

        // Power Moonroof Glass Panel Weatherstrip Seal
        PartItem(
            id = "part_sunroof_weatherstrip_seal",
            componentId = "sunroof_glass_frame_3d",
            partName = "Precision Replacement Weatherstripping Moonroof Glass Outer Perimeter Seal",
            brand = "Precision Parts",
            partNumber = "WPS 3120",
            oreillyCommercialPrice = 28.50,
            oreillyRetailPrice = 42.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 34.99, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 26.80, 5.99),
                CompetitorPrice("eBay Motors", 29.00, 0.0),
                CompetitorPrice("AutoZone", 41.99, 0.0)
            ),
            category = "Body & Interior",
            warranty = "Lifetime Warranty"
        ),

        // Sunroof Drive Motor Assembly
        PartItem(
            id = "part_sunroof_drive_motor",
            componentId = "sunroof_motor_tracks_3d",
            partName = "Dorman OE Solutions Power Sunroof Drive Motor & Gearbox Assembly",
            brand = "Dorman OE Solutions",
            partNumber = "742-263",
            oreillyCommercialPrice = 74.00,
            oreillyRetailPrice = 109.99,
            inStockLocalStore = true,
            storeStockCount = 2,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 82.50, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 69.80, 8.99),
                CompetitorPrice("eBay Motors", 71.00, 0.0),
                CompetitorPrice("AutoZone", 104.99, 0.0)
            ),
            category = "Electrical & Body",
            warranty = "Limited Lifetime Warranty"
        ),

        // Sunroof Water Drain Line Clean & Flush Kit
        PartItem(
            id = "part_sunroof_drain_cleaner_kit",
            componentId = "sunroof_drain_tubes_shade_3d",
            partName = "Performance Tool Flexible Sunroof Water Drain Hose Snake & Flush Cleaning Tool",
            brand = "Performance Tool",
            partNumber = "W80655",
            oreillyCommercialPrice = 9.80,
            oreillyRetailPrice = 14.99,
            inStockLocalStore = true,
            storeStockCount = 8,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 12.99, 0.0),
                CompetitorPrice("AutoZone", 14.99, 0.0)
            ),
            category = "Body & Accessories",
            warranty = "1 Year Warranty"
        ),

        // Front Wiper Blades 22-inch Pair
        PartItem(
            id = "part_front_wiper_blades_bosch",
            componentId = "front_windshield_3d",
            partName = "Bosch ICON All-Weather Premium Beam Wiper Blade Set (22-Inch Driver & Passenger)",
            brand = "Bosch",
            partNumber = "22A / 22B",
            oreillyCommercialPrice = 32.00,
            oreillyRetailPrice = 49.99,
            inStockLocalStore = true,
            storeStockCount = 12,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 38.99, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 31.50, 5.99),
                CompetitorPrice("AutoZone", 48.99, 0.0),
                CompetitorPrice("Advance Auto Parts", 49.99, 0.0)
            ),
            category = "Body & Exterior",
            warranty = "1 Year Warranty"
        ),

        // Front Windshield Polyurethane Adhesive Sealant
        PartItem(
            id = "part_windshield_urethane_3m",
            componentId = "front_windshield_3d",
            partName = "3M Single-Step Auto Glass Urethane Adhesive Cartridge (10.5 fl oz)",
            brand = "3M Auto",
            partNumber = "08690",
            oreillyCommercialPrice = 18.50,
            oreillyRetailPrice = 27.99,
            inStockLocalStore = true,
            storeStockCount = 6,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 22.99, 0.0),
                CompetitorPrice("AutoZone", 27.99, 0.0)
            ),
            category = "Body & Exterior",
            warranty = "90 Day Warranty"
        ),

        // Rear Drop-Down Power Window Regulator & Motor Assembly
        PartItem(
            id = "part_rear_window_regulator_dorman",
            componentId = "rear_window_power_slide_3d",
            partName = "Dorman OE Solutions Power Rear Drop-Down Sliding Window Regulator & Motor Assembly",
            brand = "Dorman OE Solutions",
            partNumber = "741-388",
            oreillyCommercialPrice = 112.00,
            oreillyRetailPrice = 164.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 129.99, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 108.50, 11.99),
                CompetitorPrice("eBay Motors", 115.00, 0.0),
                CompetitorPrice("AutoZone", 159.99, 0.0)
            ),
            category = "Electrical & Body",
            warranty = "Limited Lifetime Warranty"
        ),

        // Steering Wheel Airbag Clock Spring Harness
        PartItem(
            id = "part_airbag_clockspring_dorman",
            componentId = "airbag_driver_clockspring_3d",
            partName = "Dorman OE Solutions Steering Wheel Airbag Clock Spring Harness",
            brand = "Dorman OE Solutions",
            partNumber = "525-208",
            oreillyCommercialPrice = 58.00,
            oreillyRetailPrice = 84.99,
            inStockLocalStore = true,
            storeStockCount = 4,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 64.99, 0.0, "Prime Express"),
                CompetitorPrice("RockAuto", 52.80, 7.99),
                CompetitorPrice("eBay Motors", 56.00, 0.0),
                CompetitorPrice("AutoZone", 82.99, 0.0)
            ),
            category = "Electrical & Safety",
            warranty = "Limited Lifetime Warranty"
        ),

        // Front Crash Impact Sensor
        PartItem(
            id = "part_airbag_front_crash_sensor",
            componentId = "airbag_rcm_sensors_3d",
            partName = "Dorman OE Solutions Front Radiator Support Crash Impact Sensor",
            brand = "Dorman OE Solutions",
            partNumber = "601-012",
            oreillyCommercialPrice = 42.50,
            oreillyRetailPrice = 62.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 48.99, 0.0),
                CompetitorPrice("RockAuto", 39.50, 6.99),
                CompetitorPrice("AutoZone", 59.99, 0.0)
            ),
            category = "Electrical & Safety",
            warranty = "1 Year Warranty"
        ),

        // Seatbelt Pretensioner Buckle Anchor Assembly
        PartItem(
            id = "part_airbag_seatbelt_pretensioner",
            componentId = "airbag_seatbelt_pretensioners_3d",
            partName = "Dorman Replacement Seatbelt Buckle Anchor & Pyrotechnic Pretensioner Switch Assembly",
            brand = "Dorman",
            partNumber = "743-102",
            oreillyCommercialPrice = 78.00,
            oreillyRetailPrice = 114.99,
            inStockLocalStore = true,
            storeStockCount = 2,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 89.99, 0.0),
                CompetitorPrice("RockAuto", 72.50, 8.99),
                CompetitorPrice("AutoZone", 109.99, 0.0)
            ),
            category = "Safety & Interior",
            warranty = "Limited Lifetime Warranty"
        ),

        // A/C Compressor & Clutch
        PartItem(
            id = "part_ac_compressor_murray",
            componentId = "ac_compressor",
            partName = "Murray Climate Control FS10 A/C Compressor with Clutch Assembly",
            brand = "Murray Climate Control",
            partNumber = "58130",
            oreillyCommercialPrice = 185.00,
            oreillyRetailPrice = 249.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 199.99, 0.0, "Prime Delivery"),
                CompetitorPrice("RockAuto", 172.50, 12.99),
                CompetitorPrice("AutoZone", 244.99, 0.0)
            ),
            category = "Heating & Air Conditioning",
            warranty = "2 Year Limited Warranty"
        ),

        // Heater Core
        PartItem(
            id = "part_heater_core_murray",
            componentId = "heater_core_hvac_3d",
            partName = "Murray Heat Transfer Aluminum Heater Core Assembly",
            brand = "Murray Heat Transfer",
            partNumber = "98004",
            oreillyCommercialPrice = 42.00,
            oreillyRetailPrice = 64.99,
            inStockLocalStore = true,
            storeStockCount = 4,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 49.99, 0.0),
                CompetitorPrice("RockAuto", 38.50, 6.99),
                CompetitorPrice("AutoZone", 62.99, 0.0)
            ),
            category = "Heating & Air Conditioning",
            warranty = "Limited Lifetime Warranty"
        ),

        // Blower Motor Fan Assembly
        PartItem(
            id = "part_blower_motor_murray",
            componentId = "hvac_blower_motor_3d",
            partName = "Murray Climate Control Blower Motor Wheel Assembly",
            brand = "Murray Climate Control",
            partNumber = "35524",
            oreillyCommercialPrice = 38.50,
            oreillyRetailPrice = 54.99,
            inStockLocalStore = true,
            storeStockCount = 5,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 44.99, 0.0),
                CompetitorPrice("RockAuto", 34.25, 6.99),
                CompetitorPrice("AutoZone", 52.99, 0.0)
            ),
            category = "Heating & Air Conditioning",
            warranty = "2 Year Limited Warranty"
        ),

        // HVAC Blower Motor Resistor Pack
        PartItem(
            id = "part_blower_resistor_dorman",
            componentId = "hvac_blower_motor_3d",
            partName = "Dorman OE Solutions 4-Speed HVAC Blower Motor Resistor Pack",
            brand = "Dorman OE Solutions",
            partNumber = "973-011",
            oreillyCommercialPrice = 18.00,
            oreillyRetailPrice = 26.99,
            inStockLocalStore = true,
            storeStockCount = 7,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 21.99, 0.0),
                CompetitorPrice("RockAuto", 16.50, 4.99),
                CompetitorPrice("AutoZone", 25.99, 0.0)
            ),
            category = "Heating & Air Conditioning",
            warranty = "Limited Lifetime Warranty"
        ),

        // HVAC Electric Blend Door Actuator
        PartItem(
            id = "part_blend_door_actuator_dorman",
            componentId = "hvac_blend_door_actuator_3d",
            partName = "Dorman OE Solutions Electric HVAC Blend Door Actuator Motor",
            brand = "Dorman OE Solutions",
            partNumber = "604-203",
            oreillyCommercialPrice = 32.00,
            oreillyRetailPrice = 47.99,
            inStockLocalStore = true,
            storeStockCount = 6,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 36.99, 0.0),
                CompetitorPrice("RockAuto", 28.50, 5.99),
                CompetitorPrice("AutoZone", 46.99, 0.0)
            ),
            category = "Heating & Air Conditioning",
            warranty = "Limited Lifetime Warranty"
        ),

        // A/C Accumulator Receiver Drier
        PartItem(
            id = "part_ac_accumulator_murray",
            componentId = "ac_evaporator_accumulator_3d",
            partName = "Murray Climate Control A/C Accumulator Drier Bottle & Orifice Tube",
            brand = "Murray Climate Control",
            partNumber = "33211",
            oreillyCommercialPrice = 28.50,
            oreillyRetailPrice = 41.99,
            inStockLocalStore = true,
            storeStockCount = 3,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 32.99, 0.0),
                CompetitorPrice("RockAuto", 24.50, 6.99),
                CompetitorPrice("AutoZone", 39.99, 0.0)
            ),
            category = "Heating & Air Conditioning",
            warranty = "2 Year Limited Warranty"
        ),

        // A/C Condenser Core
        PartItem(
            id = "part_ac_condenser_murray",
            componentId = "ac_condenser_lines_3d",
            partName = "Murray Heat Transfer Heavy Duty Parallel Flow A/C Condenser Core",
            brand = "Murray Heat Transfer",
            partNumber = "4988",
            oreillyCommercialPrice = 88.00,
            oreillyRetailPrice = 124.99,
            inStockLocalStore = true,
            storeStockCount = 2,
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 98.99, 0.0),
                CompetitorPrice("RockAuto", 81.50, 9.99),
                CompetitorPrice("AutoZone", 119.99, 0.0)
            ),
            category = "Heating & Air Conditioning",
            warranty = "Limited Lifetime Warranty"
        ),

        // O'REILLY LOAN-A-TOOL COMMERCIAL PROGRAM ($0 NET COST UPON RETURN)
        PartItem(
            id = "part_tool_digital_multimeter",
            componentId = "wiring_lighting_3d",
            partName = "EverTough Auto Electrical Multimeter & Wire Crimper Set",
            brand = "EverTough Loan-A-Tool",
            partNumber = "TOOL-67201",
            oreillyCommercialPrice = 0.00,
            oreillyRetailPrice = 45.00,
            inStockLocalStore = true,
            storeStockCount = 4,
            category = "Specialty Tools",
            warranty = "Free Rental (Return within 90 days for 100% refund)",
            isLoanerTool = true
        ),
        PartItem(
            id = "part_tool_fuel_pressure_tester",
            componentId = "spark_plugs_40l",
            partName = "EverTough Master Fuel Pressure Test Kit",
            brand = "EverTough Loan-A-Tool",
            partNumber = "TOOL-67003",
            oreillyCommercialPrice = 0.00,
            oreillyRetailPrice = 89.00,
            inStockLocalStore = true,
            storeStockCount = 2,
            category = "Specialty Tools",
            warranty = "Free Rental (100% Deposit Refund)",
            isLoanerTool = true
        ),
        PartItem(
            id = "part_tool_spring_compressor",
            componentId = "front_brakes",
            partName = "EverTough Heavy Duty Torsion Bar & Coil Spring Compressor",
            brand = "EverTough Loan-A-Tool",
            partNumber = "TOOL-67022",
            oreillyCommercialPrice = 0.00,
            oreillyRetailPrice = 110.00,
            inStockLocalStore = true,
            storeStockCount = 3,
            category = "Specialty Tools",
            warranty = "Free Rental (100% Deposit Refund)",
            isLoanerTool = true
        ),
        PartItem(
            id = "part_tool_brake_bleeder",
            componentId = "abs_master_cylinder_3d",
            partName = "EverTough One-Man Vacuum Brake Bleeder & Caliper Press Kit",
            brand = "EverTough Loan-A-Tool",
            partNumber = "TOOL-67011",
            oreillyCommercialPrice = 0.00,
            oreillyRetailPrice = 65.00,
            inStockLocalStore = true,
            storeStockCount = 3,
            category = "Specialty Tools",
            warranty = "Free Rental (100% Deposit Refund)",
            isLoanerTool = true
        ),
        PartItem(
            id = "part_tool_exhaust_cutter",
            componentId = "catback_exhaust_muffler_3d",
            partName = "EverTough Exhaust Pipe Tailpipe Cutter & Rubber Hanger Pliers Set",
            brand = "EverTough Loan-A-Tool",
            partNumber = "TOOL-67035",
            oreillyCommercialPrice = 0.00,
            oreillyRetailPrice = 55.00,
            inStockLocalStore = true,
            storeStockCount = 2,
            category = "Specialty Tools",
            warranty = "Free Rental (100% Deposit Refund)",
            isLoanerTool = true
        ),

        // --- RADIO & AUDIO SYSTEM PARTS ---
        PartItem(
            id = "part_radio_head_unit",
            componentId = "radio_mach500_head_unit_3d",
            partName = "Ford Mach 500 Double-DIN AM/FM 6-Disc In-Dash CD Changer Radio Unit",
            brand = "Motorcraft / Ford OEM Parts",
            partNumber = "1L2F-18C815-AA",
            oreillyCommercialPrice = 189.99,
            oreillyRetailPrice = 249.99,
            inStockLocalStore = true,
            storeStockCount = 1,
            category = "Dash & Interior Electronics",
            warranty = "2 Year / Unlimited Mileage Warranty",
            competitorPrices = listOf(
                CompetitorPrice("RockAuto", 175.00, 12.99, "Remanufactured Core Exchange"),
                CompetitorPrice("eBay Motors", 165.00, 0.0, "OEM Refurbished"),
                CompetitorPrice("Crutchfield", 199.99, 0.0, "Includes Harness Kit")
            )
        ),
        PartItem(
            id = "part_door_speakers_6x8",
            componentId = "audio_door_speakers_subwoofer_3d",
            partName = "Pioneer TS-A683FH 6x8 3-Way Coaxial High-Output Door Speakers (Pair)",
            brand = "Pioneer Audio",
            partNumber = "TS-A683FH",
            oreillyCommercialPrice = 54.99,
            oreillyRetailPrice = 69.99,
            inStockLocalStore = true,
            storeStockCount = 4,
            category = "Door Speakers & Audio",
            warranty = "1 Year Limited Warranty",
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 59.99, 0.0, "Prime 2-Day"),
                CompetitorPrice("Crutchfield", 64.99, 0.0, "Free Mounting Adapters"),
                CompetitorPrice("Best Buy", 69.99, 0.0, "In Store Pickup")
            )
        ),
        PartItem(
            id = "part_radio_wiring_harness",
            componentId = "radio_mach500_head_unit_3d",
            partName = "Metra 70-5521 Ford Radio Plug Wiring Harness & Antenna Coax Adapter Kit",
            brand = "Metra Electronics",
            partNumber = "70-5521",
            oreillyCommercialPrice = 11.99,
            oreillyRetailPrice = 16.99,
            inStockLocalStore = true,
            storeStockCount = 8,
            category = "Stereo Wiring & Install Accessories",
            warranty = "1 Year Warranty",
            competitorPrices = listOf(
                CompetitorPrice("Amazon Prime", 12.50, 0.0, "Prime Delivery"),
                CompetitorPrice("Crutchfield", 14.99, 0.0, "Included with Receiver")
            )
        ),
        PartItem(
            id = "part_tool_din_keys",
            componentId = "radio_mach500_head_unit_3d",
            partName = "Ford U-Shaped DIN Radio Removal Key Tool Set (Ford # T83P-19061-A)",
            brand = "Performance Tool",
            partNumber = "W80655",
            oreillyCommercialPrice = 5.99,
            oreillyRetailPrice = 8.99,
            inStockLocalStore = true,
            storeStockCount = 6,
            category = "Specialty Tools",
            warranty = "Lifetime Warranty",
            competitorPrices = listOf(
                CompetitorPrice("AutoZone", 8.49, 0.0, "Retail Counter"),
                CompetitorPrice("Amazon Prime", 6.99, 0.0, "Prime Delivery")
            )
        ),
        PartItem(
            id = "part_subwoofer_pioneer",
            componentId = "audio_door_speakers_subwoofer_3d",
            partName = "Pioneer 8-Inch Shallow-Mount 2-Ohm Subwoofer Driver & Amp Fuse Kit",
            brand = "Pioneer Electronics",
            partNumber = "TS-A2000LD2",
            oreillyCommercialPrice = 89.99,
            oreillyRetailPrice = 119.99,
            inStockLocalStore = true,
            storeStockCount = 2,
            category = "Subwoofers & Amplifiers",
            warranty = "1 Year Warranty",
            competitorPrices = listOf(
                CompetitorPrice("Crutchfield", 109.99, 0.0, "Tech Support Included"),
                CompetitorPrice("Amazon Prime", 94.99, 0.0, "Prime 2-Day")
            )
        )
    )

    fun getPartsForComponent(componentId: String): List<PartItem> {
        val matches = catalog.filter { it.componentId == componentId }
        return if (matches.isNotEmpty()) {
            matches
        } else {
            // Fallback generic OEM replacement part for any component
            listOf(
                PartItem(
                    id = "part_generic_$componentId",
                    componentId = componentId,
                    partName = "Motorcraft Premium OEM Direct Replacement Part",
                    brand = "Motorcraft",
                    partNumber = "MC-2004ST-$componentId",
                    oreillyCommercialPrice = 34.99,
                    oreillyRetailPrice = 49.99,
                    inStockLocalStore = true,
                    storeStockCount = 5,
                    competitorPrices = listOf(
                        CompetitorPrice("Amazon Prime", 36.50, 0.0, "Prime Delivery"),
                        CompetitorPrice("RockAuto", 32.50, 6.99, "Ships in 2 Days"),
                        CompetitorPrice("eBay Motors", 35.00, 0.0, "Top Rated Seller"),
                        CompetitorPrice("AutoZone", 47.99, 0.0, "Retail Counter"),
                        CompetitorPrice("Advance Auto Parts", 49.99, 0.0, "In Store"),
                        CompetitorPrice("NAPA Auto Parts", 48.50, 0.0, "Local Store"),
                        CompetitorPrice("Summit Racing", 38.99, 6.95, "Warehouse Stock")
                    )
                )
            )
        }
    }
}
