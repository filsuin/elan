package fr.elan.api

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import java.io.FileInputStream
import java.util.concurrent.TimeUnit

private const val UPDATE_TOPIC = "elan-app-updates"
private const val FCM_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"

internal fun sendUpdateNotification(versionCode: Int, versionName: String, downloadUrl: String): String {
    val projectId = System.getenv("FCM_PROJECT_ID")?.takeIf { it.isNotBlank() }
        ?: error("FCM_PROJECT_ID absent")
    val credentialsPath = System.getenv("FCM_SERVICE_ACCOUNT_PATH")?.takeIf { it.isNotBlank() }
        ?: error("FCM_SERVICE_ACCOUNT_PATH absent")
    val firebaseApp = FirebaseApp.getApps().firstOrNull { it.name == "elan-fcm" } ?: run {
        val credentials = FileInputStream(credentialsPath).use {
            GoogleCredentials.fromStream(it).createScoped(FCM_SCOPE)
        }
        FirebaseApp.initializeApp(
            FirebaseOptions.builder().setCredentials(credentials).setProjectId(projectId).build(),
            "elan-fcm",
        )
    }
    val message = Message.builder()
        .setTopic(UPDATE_TOPIC)
        .putData("versionCode", versionCode.toString())
        .putData("versionName", versionName)
        .putData("downloadUrl", downloadUrl)
        .setAndroidConfig(AndroidConfig.builder()
            .setPriority(AndroidConfig.Priority.HIGH)
            .setTtl(TimeUnit.HOURS.toMillis(1))
            .build())
        .build()
    return FirebaseMessaging.getInstance(firebaseApp).send(message)
}
