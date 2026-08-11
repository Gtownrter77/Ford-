package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MaintenanceAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(MaintenanceSchedulerService.EXTRA_NOTIFICATION_ID, 1001)
        val title = intent.getStringExtra(MaintenanceSchedulerService.EXTRA_TITLE) ?: "Sport Trac Maintenance Alert"
        val message = intent.getStringExtra(MaintenanceSchedulerService.EXTRA_MESSAGE) ?: "Upcoming service milestone is due for your vehicle."

        val scheduler = MaintenanceSchedulerService(context)
        scheduler.triggerLocalNotification(id, title, message)
    }
}
