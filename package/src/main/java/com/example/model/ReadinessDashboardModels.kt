package com.example.model

enum class DashboardAlertKind(
    val label: String,
    val description: String
) {
    WEEKLY_PRICE_REVIEW("Weekly price review", "Review saved prices, shipping, core charges, seller details, and fitment evidence for watched parts."),
    MAINTENANCE_WINDOW("Maintenance window", "Review a scheduled mileage or time-based service item before it becomes overdue."),
    SEASONAL_PREP("Seasonal preparation", "Prepare the applicable vehicle system before a weather-related season or temperature change."),
    FITMENT_QUEUE("Fitment queue", "Complete VIN, under-hood-label, capacity, or configuration verification before ordering a pending item.")
}

data class DashboardAlertRule(
    val id: String,
    val kind: DashboardAlertKind,
    val title: String,
    val detail: String,
    val packageId: String? = null,
    val priority: Int,
    val requiresVerification: Boolean = false
)
