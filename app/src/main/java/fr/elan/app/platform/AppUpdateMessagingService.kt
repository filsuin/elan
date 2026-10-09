package fr.elan.app.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.elan.app.BuildConfig
import fr.elan.app.R

class AppUpdateMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val versionName = message.data["versionName"]?.takeIf { Regex("^\\d+\\.\\d+\\.\\d+$").matches(it) } ?: return
        val versionCode = message.data["versionCode"]?.toIntOrNull() ?: return
        if (versionCode <= BuildConfig.VERSION_CODE) return
        val downloadUrl = message.data["downloadUrl"] ?: return
        val uri = runCatching { Uri.parse(downloadUrl) }.getOrNull() ?: return
        if (uri.scheme != "https" || uri.host != "elan.maxremonnay.fr") return

        getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(
            CHANNEL, "Mises à jour d’Élan", NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = "Nouvelles versions de l’application Élan." })

        val openPage = PendingIntent.getActivity(
            this, NOTIFICATION_ID, Intent(Intent.ACTION_VIEW, uri),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Élan $versionName est disponible")
            .setContentText("Télécharge la nouvelle version sur le site d’Élan.")
            .setContentIntent(openPage)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled() ||
            (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED)
        ) return
        runCatching { NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification) }
    }

    companion object {
        private const val CHANNEL = "elan-app-updates"
        private const val NOTIFICATION_ID = 3101
    }
}
