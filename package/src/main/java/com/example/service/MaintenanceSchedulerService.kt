package com.example.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.local.MaintenanceEntity
import com.example.data.local.UpcomingTaskEntity
import com.example.model.MaintenanceScheduleItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MaintenanceSchedulerService(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "sport_trac_maintenance_reminders"
        const val CHANNEL_NAME = "Sport Trac Maintenance Reminders"
        const val CHANNEL_DESC = "Notifications for upcoming Ford Sport Trac service intervals and maintenance milestones"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun triggerLocalNotification(id: Int, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id, notification)
    }

    fun scheduleFutureReminder(id: Int, title: String, message: String, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MaintenanceAlarmReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, id)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelScheduledReminder(id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MaintenanceAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun scheduleMaintenanceRemindersFromRoomLogs(
        logs: List<MaintenanceEntity>,
        schedules: List<MaintenanceScheduleItem>,
        upcomingTasks: List<UpcomingTaskEntity> = emptyList(),
        currentMileage: Int
    ): List<ScheduledReminderAlert> {
        val alerts = mutableListOf<ScheduledReminderAlert>()
        val nowMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        // 1. Process custom / Room DB upcoming tasks
        upcomingTasks.filter { !it.isCompleted }.forEach { task ->
            val milesRemaining = task.targetMileage - currentMileage
            val daysRemaining = ((task.dueDateMillis - nowMillis) / 86400000L).toInt()
            val notificationId = ("task_" + task.id).hashCode()

            val title = if (milesRemaining <= 0 || daysRemaining <= 0) {
                "⚠️ OVERDUE SERVICE: ${task.title}"
            } else if (milesRemaining <= 1000 || daysRemaining <= 7) {
                "🔔 SERVICE DUE SOON: ${task.title}"
            } else {
                "🗓️ Scheduled Service: ${task.title}"
            }

            val dueDateStr = if (task.dueDateMillis > 0) dateFormat.format(Date(task.dueDateMillis)) else "N/A"
            val specStr = if (task.fluidSpecOrPart.isNotBlank()) " | Spec: ${task.fluidSpecOrPart}" else ""

            val message = if (milesRemaining <= 0) {
                "Overdue by ${-milesRemaining} miles! Due at %,d miles (Current: %,d miles). Target date: %s%s.".format(
                    task.targetMileage, currentMileage, dueDateStr, specStr
                )
            } else if (daysRemaining <= 0) {
                "Overdue by ${-daysRemaining} days (Target Date: %s)! Current mileage: %,d / %,d miles%s.".format(
                    dueDateStr, currentMileage, task.targetMileage, specStr
                )
            } else {
                "Due in %,d miles (%d days left on %s) at %,d miles%s.".format(
                    milesRemaining, daysRemaining, dueDateStr, task.targetMileage, specStr
                )
            }

            val isDueSoon = milesRemaining <= 1000 || daysRemaining <= 7
            val isOverdue = milesRemaining <= 0 || daysRemaining <= 0

            alerts.add(
                ScheduledReminderAlert(
                    id = notificationId,
                    dbTaskId = task.id,
                    scheduleItemId = task.scheduleItemId,
                    title = title,
                    message = message,
                    systemName = task.systemName,
                    priorityLevel = task.priorityLevel,
                    milesRemaining = milesRemaining,
                    nextDueMileage = task.targetMileage,
                    dueDateMillis = task.dueDateMillis,
                    isDueSoon = isDueSoon,
                    isOverdue = isOverdue
                )
            )
        }

        // 2. Process factory schedules
        schedules.forEach { item ->
            val matchingLog = logs.firstOrNull {
                it.scheduleItemId == item.id || it.title.contains(item.title, ignoreCase = true)
            }

            val lastMileage = matchingLog?.mileageAtService ?: (currentMileage - (item.intervalMiles * 0.6).toInt())
            val nextDueMiles = lastMileage + item.intervalMiles
            val milesRemaining = nextDueMiles - currentMileage

            val notificationId = ("sched_" + item.id).hashCode()

            // Skip if already represented in upcomingTasks
            val existsInTasks = upcomingTasks.any {
                it.scheduleItemId == item.id || it.title.contains(item.title, ignoreCase = true)
            }

            if (!existsInTasks) {
                val title = if (milesRemaining <= 0) {
                    "⚠️ OVERDUE MAINTENANCE: ${item.title}"
                } else if (milesRemaining <= 1000) {
                    "🔔 MILESTONE DUE SOON: ${item.title}"
                } else {
                    "🔧 Scheduled Maintenance: ${item.title}"
                }

                val message = if (milesRemaining <= 0) {
                    "OVERDUE! ${item.title} was due at %,d miles (Current: %,d miles). Spec: %s.".format(
                        nextDueMiles, currentMileage, item.fluidTypeOrSpec
                    )
                } else if (milesRemaining <= 1000) {
                    "UPCOMING SERVICE! ${item.title} due in %,d miles at %,d miles. Spec: %s.".format(
                        milesRemaining, nextDueMiles, item.fluidTypeOrSpec
                    )
                } else {
                    "Scheduled Service: ${item.title} due at %,d miles (%,d miles remaining).".format(
                        nextDueMiles, milesRemaining
                    )
                }

                val isDueSoon = milesRemaining <= 1000
                val isOverdue = milesRemaining <= 0

                alerts.add(
                    ScheduledReminderAlert(
                        id = notificationId,
                        dbTaskId = null,
                        scheduleItemId = item.id,
                        title = title,
                        message = message,
                        systemName = item.system.displayName,
                        priorityLevel = if (isOverdue) "CRITICAL" else if (isDueSoon) "HIGH" else "NORMAL",
                        milesRemaining = milesRemaining,
                        nextDueMileage = nextDueMiles,
                        dueDateMillis = 0L,
                        isDueSoon = isDueSoon,
                        isOverdue = isOverdue
                    )
                )
            }
        }

        return alerts.sortedWith(compareByDescending<ScheduledReminderAlert> { it.isOverdue }.thenByDescending { it.isDueSoon })
    }

    fun checkAndPostUrgentNotifications(
        upcomingTasks: List<UpcomingTaskEntity>,
        currentMileage: Int
    ): Int {
        var postedCount = 0
        val nowMillis = System.currentTimeMillis()

        upcomingTasks.filter { !it.isCompleted }.forEach { task ->
            val milesRemaining = task.targetMileage - currentMileage
            val daysRemaining = ((task.dueDateMillis - nowMillis) / 86400000L).toInt()

            val isOverdue = milesRemaining <= 0 || (task.dueDateMillis > 0 && task.dueDateMillis <= nowMillis)
            val isDueSoon = (milesRemaining in 1..1000) || (task.dueDateMillis > 0 && daysRemaining in 1..7)

            if (isOverdue || isDueSoon) {
                val notificationId = ("task_" + task.id).hashCode()
                val title = if (isOverdue) "⚠️ OVERDUE: ${task.title}" else "🔔 DUE SOON: ${task.title}"
                val message = "Target: %,d miles | Spec: %s | Priority: %s".format(
                    task.targetMileage,
                    if (task.fluidSpecOrPart.isNotBlank()) task.fluidSpecOrPart else "Ford OEM Spec",
                    task.priorityLevel
                )

                triggerLocalNotification(notificationId, title, message)
                postedCount++
            }
        }

        return postedCount
    }
}

data class ScheduledReminderAlert(
    val id: Int,
    val dbTaskId: Long? = null,
    val scheduleItemId: String,
    val title: String,
    val message: String,
    val systemName: String = "Engine",
    val priorityLevel: String = "NORMAL",
    val milesRemaining: Int,
    val nextDueMileage: Int,
    val dueDateMillis: Long = 0L,
    val isDueSoon: Boolean,
    val isOverdue: Boolean = false
)

