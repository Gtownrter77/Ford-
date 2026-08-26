package com.example.data

import com.example.model.Component3DModel

/**
 * CHARM workshop leaves for the 2004 Explorer Sport Trac 4WD V6-4.0L VIN K only.
 * 2WD pack URLs are recorded so they can be rejected, never recommended.
 */
data class CharmWorkshopLeaf(
    val title: String,
    val url: String,
    val keywords: List<String>
)

object CharmWorkshopIndex {
    const val VEHICLE_TREE = "Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel"
    const val HUB =
        "https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/"
    const val DISCLAIMER =
        "CHARM is a third-party copy of workshop pages. Confirm the 4WD VIN K tree before using a procedure."

    private const val ROOT =
        "https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/"

    val leaves: List<CharmWorkshopLeaf> = listOf(
        CharmWorkshopLeaf("4WD VIN K hub", HUB, listOf("workshop", "charm", "manual", "fsm", "hub")),
        CharmWorkshopLeaf(
            "Repair and Diagnosis",
            ROOT + "Repair%20and%20Diagnosis/",
            listOf("repair", "diagnosis", "workshop")
        ),
        CharmWorkshopLeaf(
            "Engine",
            ROOT + "Repair%20and%20Diagnosis/Engine%2C%20Cooling%20and%20Exhaust/Engine/",
            listOf("engine", "block", "cologne")
        ),
        CharmWorkshopLeaf(
            "Drive belts",
            ROOT + "Repair%20and%20Diagnosis/Engine%2C%20Cooling%20and%20Exhaust/Engine/Drive%20Belts/",
            listOf("belt", "serpentine", "tensioner")
        ),
        CharmWorkshopLeaf(
            "Cooling system",
            ROOT + "Repair%20and%20Diagnosis/Engine%2C%20Cooling%20and%20Exhaust/Cooling%20System/",
            listOf("coolant", "cooling", "thermostat", "radiator")
        ),
        CharmWorkshopLeaf(
            "Transmission speed sensor",
            ROOT + "Repair%20and%20Diagnosis/Transmission%20and%20Drivetrain/Automatic%20Transmission%2FTransaxle/Transmission%20Speed%20Sensor/",
            listOf("transmission", "5r55e", "speed sensor", "shift")
        ),
        CharmWorkshopLeaf(
            "Transfer case",
            ROOT + "Repair%20and%20Diagnosis/Transmission%20and%20Drivetrain/Transfer%20Case/",
            listOf("transfer", "4wd", "4x4", "shift motor")
        ),
        CharmWorkshopLeaf(
            "Drive axles",
            ROOT + "Repair%20and%20Diagnosis/Transmission%20and%20Drivetrain/Drive%20Axles/",
            listOf("axle", "cv", "driveshaft", "diff")
        ),
        CharmWorkshopLeaf(
            "Suspension",
            ROOT + "Repair%20and%20Diagnosis/Steering%20and%20Suspension/Suspension/",
            listOf("suspension", "control arm", "ball joint", "shock", "sway")
        ),
        CharmWorkshopLeaf(
            "Steering",
            ROOT + "Repair%20and%20Diagnosis/Steering%20and%20Suspension/Steering/",
            listOf("steering", "rack", "power steering")
        ),
        CharmWorkshopLeaf(
            "Brakes",
            ROOT + "Repair%20and%20Diagnosis/Brakes%20and%20Traction%20Control/Brakes/",
            listOf("brake", "rotor", "caliper", "pad", "abs")
        ),
        CharmWorkshopLeaf(
            "HVAC (4WD)",
            ROOT + "Repair%20and%20Diagnosis/Heating%20and%20Air%20Conditioning/",
            listOf("hvac", "a/c", "ac ", "air condition", "compressor")
        ),
        CharmWorkshopLeaf(
            "A/C compressor service (4WD)",
            ROOT + "Repair%20and%20Diagnosis/Heating%20and%20Air%20Conditioning/Compressor%20HVAC/Service%20and%20Repair/",
            listOf("compressor", "a/c", "ac compressor")
        ),
        CharmWorkshopLeaf(
            "Body general",
            ROOT + "Repair%20and%20Diagnosis/Body%20and%20Frame/Description%20and%20Operation/Body%20System%20-%20General%20Information/",
            listOf("body", "frame")
        ),
        CharmWorkshopLeaf(
            "Windows and glass",
            ROOT + "Repair%20and%20Diagnosis/Windows%20and%20Glass/",
            listOf("window", "regulator", "glass")
        ),
        CharmWorkshopLeaf(
            "All DTCs",
            ROOT + "Repair%20and%20Diagnosis/A%20L%20L%20%20Diagnostic%20Trouble%20Codes%20%28%20DTC%20%29/",
            listOf("dtc", "code", "p0", "obd")
        )
    )

    val rejectedTwoWheelDriveUrls: Set<String> = setOf(
        "https://charm.li/Ford/2004/Explorer%20Sport%20Trac%202WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/Specifications/Mechanical%20Specifications/Firing%20Order/",
        "https://charm.li/Ford/2004/Explorer%20Sport%20Trac%202WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/Heating%20and%20Air%20Conditioning/Compressor%20HVAC/Service%20and%20Repair/"
    )

    fun isRejectedTwoWheelDriveUrl(url: String): Boolean {
        return url.contains("Sport%20Trac%202WD") ||
            url.contains("Sport Trac 2WD") ||
            rejectedTwoWheelDriveUrls.contains(url)
    }

    fun matching(query: String, component: Component3DModel? = null): List<CharmWorkshopLeaf> {
        val haystack = buildString {
            append(query.lowercase())
            if (component != null) {
                append(' ')
                append(component.name.lowercase())
                append(' ')
                append(component.id.lowercase())
                append(' ')
                append(component.system.displayName.lowercase())
            }
        }
        val hits = leaves.filter { leaf ->
            !isRejectedTwoWheelDriveUrl(leaf.url) &&
                leaf.keywords.any { keyword -> haystack.contains(keyword) }
        }.distinctBy { it.url }
        return if (hits.isEmpty()) {
            listOf(leaves.first())
        } else {
            hits
        }
    }

    fun format(leavesToShow: List<CharmWorkshopLeaf>, limit: Int = 3): String {
        val safe = leavesToShow.filterNot { isRejectedTwoWheelDriveUrl(it.url) }.take(limit)
        if (safe.isEmpty()) {
            return "No 4WD VIN K CHARM leaf matches that request. Hub: $HUB. $DISCLAIMER"
        }
        return safe.joinToString(" ") { leaf ->
            "${leaf.title}: ${leaf.url}."
        } + " $DISCLAIMER"
    }
}
