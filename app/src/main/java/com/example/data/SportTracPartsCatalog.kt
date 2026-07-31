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
