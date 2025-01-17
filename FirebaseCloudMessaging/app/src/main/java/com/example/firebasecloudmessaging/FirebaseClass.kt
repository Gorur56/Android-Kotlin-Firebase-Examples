package com.example.firebasecloudmessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseClass : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Yeni token: $token")
        // Token'ı sunucunuza gönderin
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Mesaj geldi: ${remoteMessage.data}")

        // Bildirim oluştur
        remoteMessage.notification?.let {
            val title = it.title
            val body = it.body
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        // Bildirimi oluştur ve göster
        val channelId = "Default_Channel_ID"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .build()

        notificationManager.notify(0, notification)
    }
}