package fr.elan.app.platform

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import fr.elan.app.ElanApplication
import fr.elan.app.MainActivity
import fr.elan.app.R
import fr.elan.app.domain.UserSettings
import fr.elan.app.domain.nextReminder
import java.time.ZonedDateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

object ReminderScheduler {
    const val ACTION = "fr.elan.app.DAILY_REMINDER"
    private const val CHANNEL = "elan-gentle-reminders"
    private const val REQUEST = 2609
    private fun pending(context: Context): PendingIntent = PendingIntent.getBroadcast(context, REQUEST,
        Intent(context, ReminderReceiver::class.java).setAction(ACTION), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    fun allowed(context: Context): Boolean {
        val channel = context.getSystemService(NotificationManager::class.java).getNotificationChannel(CHANNEL)
        return NotificationManagerCompat.from(context).areNotificationsEnabled() &&
            channel?.importance != NotificationManager.IMPORTANCE_NONE &&
            (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }
    @Synchronized
    fun schedule(context: Context, settings: UserSettings, force: Boolean = false, restore: Boolean = false) {
        val manager = context.getSystemService(AlarmManager::class.java)
        val intent = pending(context)
        // Operational metadata only; the user's reminder choice stays in DataStore.
        val metadata = context.getSharedPreferences("elan-reminder-schedule", Context.MODE_PRIVATE)
        if (!settings.reminderEnabled) {
            manager.cancel(intent); metadata.edit().clear().apply()
            NotificationManagerCompat.from(context).cancel(REQUEST)
            return
        }
        val now = ZonedDateTime.now()
        val key = "${settings.reminderHour}:${settings.reminderMinute}:${now.zone.id}"
        val scheduled = metadata.getLong("scheduledAt", 0)
        val age = now.toInstant().toEpochMilli() - scheduled
        // Give a delayed alarm its chance to fire, including while the app is in the foreground.
        val samePending = metadata.getString("key", null) == key && scheduled > 0 && age < 12 * 60 * 60 * 1000L
        if (!force && !restore && samePending) return
        // Reinstall the pending alarm on process startup, including after a force-stop.
        // Keep an overdue inexact alarm instead of silently postponing it to tomorrow.
        val timeMillis = if (restore && samePending)
            maxOf(scheduled, now.toInstant().toEpochMilli() + 1000L)
        else nextReminder(now, settings.reminderHour, settings.reminderMinute).toInstant().toEpochMilli()
        manager.cancel(intent)
        // Inexact by design. No SCHEDULE_EXACT_ALARM permission; Android may delay delivery.
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, intent)
        metadata.edit().putString("key", key).putLong("scheduledAt", timeMillis).apply()
    }
    fun notify(context: Context) {
        if (!allowed(context)) return
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Rappel bien-être", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Une invitation quotidienne facultative, sans information personnelle."
        })
        val open = PendingIntent.getActivity(context, REQUEST,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification).setContentTitle("Un petit moment pour toi")
            .setContentText("Une pause, quelques mouvements ou ton journal : choisis ton rythme.")
            .setContentIntent(open).setAutoCancel(true).setVisibility(NotificationCompat.VISIBILITY_PRIVATE).build()
        // Permission may be revoked between the check and this call.
        try { manager.notify(REQUEST, notification) } catch (_: SecurityException) { /* Respect denial. */ }
    }
}

/** System broadcasts only, plus an explicit app PendingIntent. Never starts an activity itself. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val recognized = setOf(ReminderScheduler.ACTION, Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_MY_PACKAGE_REPLACED, UpdateScheduler.ACTION)
        if (intent.action !in recognized) return
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                withTimeout(8000) {
                    if (intent.action == UpdateScheduler.ACTION) {
                        UpdateChecker.checkAndNotify(context)
                        return@withTimeout
                    }
                    if (intent.action in setOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED)) {
                        UpdateScheduler.schedule(context)
                    }
                    val settings = (context.applicationContext as ElanApplication).repository.data.first().settings
                    if (intent.action == ReminderScheduler.ACTION && settings.reminderEnabled) ReminderScheduler.notify(context)
                    ReminderScheduler.schedule(context, settings, force = true)
                }
            } catch (_: Exception) {
                // The next foreground open reschedules; no journal is reset on a read failure.
            } finally { pending.finish() }
        }
    }
}

