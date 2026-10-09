package fr.elan.app.platform

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import fr.elan.app.BuildConfig
import fr.elan.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {
    private const val CHANNEL = "elan-app-updates"
    private const val NOTIFICATION_ID = 3101
    private const val CHECK_INTERVAL = 6 * 60 * 60 * 1000L
    private val checkLock = Mutex()

    suspend fun checkAndNotify(context: Context) = checkLock.withLock {
        val app = context.applicationContext
        val prefs = app.getSharedPreferences("elan-updates", Context.MODE_PRIVATE)
        if (System.currentTimeMillis() - prefs.getLong("lastCheck", 0L) < CHECK_INTERVAL) return@withLock

        val release = withContext(Dispatchers.IO) { fetchLatestUpdate() } ?: return@withLock
        prefs.edit().putLong("lastCheck", System.currentTimeMillis()).apply()
        if (release.versionCode <= BuildConfig.VERSION_CODE) return@withLock
        if (prefs.getInt("lastNotified", -1) == release.versionCode) return@withLock

        val manager = app.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Mises à jour d’Élan", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Annonce les nouvelles versions disponibles de l’application."
        })
        if (!NotificationManagerCompat.from(app).areNotificationsEnabled() ||
            (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(app, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
        ) return@withLock

        val page = PendingIntent.getActivity(app, NOTIFICATION_ID,
            Intent(Intent.ACTION_VIEW, Uri.parse(release.pageUrl)),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(app, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Élan ${release.versionName} est disponible")
            .setContentText("Télécharge la nouvelle version sur le site d’Élan.")
            .setContentIntent(page)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()
        try {
            NotificationManagerCompat.from(app).notify(NOTIFICATION_ID, notification)
            prefs.edit().putInt("lastNotified", release.versionCode).apply()
        } catch (_: SecurityException) {
            // Respecte le refus ou la révocation de l’autorisation Android.
        }
    }

    private fun fetchLatestUpdate(): Release? {
        val connection = (URL(BuildConfig.UPDATE_MANIFEST_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Elan-Android/${BuildConfig.VERSION_NAME}")
        }
        return try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null
            val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            val versionCode = json.optInt("versionCode", -1)
            val versionName = json.optString("versionName")
            val pageUrl = json.optString("downloadPageUrl")
            val endpoint = Uri.parse(BuildConfig.UPDATE_MANIFEST_URL)
            val page = Uri.parse(pageUrl)
            if (versionCode <= 0 || !Regex("^\\d+\\.\\d+\\.\\d+$").matches(versionName) ||
                endpoint.scheme != "https" || endpoint.host.isNullOrBlank() || page.scheme != "https" || page.host != endpoint.host
            ) null else Release(versionCode, versionName, pageUrl)
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private data class Release(val versionCode: Int, val versionName: String, val pageUrl: String)
}

object UpdateScheduler {
    const val ACTION = "fr.elan.app.CHECK_APP_UPDATE"
    private const val REQUEST = 3102
    private const val INTERVAL = AlarmManager.INTERVAL_DAY

    fun schedule(context: Context) {
        val intent = PendingIntent.getBroadcast(context, REQUEST,
            Intent(context, ReminderReceiver::class.java).setAction(ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarm = context.getSystemService(AlarmManager::class.java)
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            android.os.SystemClock.elapsedRealtime() + INTERVAL, INTERVAL, intent)
    }
}
