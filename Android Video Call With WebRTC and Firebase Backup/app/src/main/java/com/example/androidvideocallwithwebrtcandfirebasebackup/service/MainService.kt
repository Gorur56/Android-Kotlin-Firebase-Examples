package com.example.androidvideocallwithwebrtcandfirebasebackup.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.androidvideocallwithwebrtcandfirebasebackup.R
import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModel
import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModelType
import com.example.androidvideocallwithwebrtcandfirebasebackup.data.isValid
import com.example.androidvideocallwithwebrtcandfirebasebackup.repository.MainRepository
import com.example.androidvideocallwithwebrtcandfirebasebackup.repository.MainRepositoryListener
import dagger.hilt.android.AndroidEntryPoint
import com.example.androidvideocallwithwebrtcandfirebasebackup.service.MainServiceActions.*
import javax.inject.Inject

@AndroidEntryPoint
class MainService : Service(), MainRepositoryListener {
    private val TAG = "MainService"

    private var isServiceRunning = false
    private var username: String ?= null

    @Inject lateinit var mainRepository: MainRepository

    private lateinit var notificationManager: NotificationManager

    companion object {
        var listener: MainServiceListener ?= null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(
            NotificationManager::class.java
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { incomingIntent ->
            when(incomingIntent.action) {
                START_SERVICE.name -> handleStartService(incomingIntent)
                else -> Unit
            }
        }

        return START_STICKY
    }

    private fun handleStartService(incomingIntent: Intent) {
        //start our foreground service
        if (!isServiceRunning) {
            isServiceRunning = true
            username = incomingIntent.getStringExtra("username")
            startServiceWithNotification()

            //setup my clients
            mainRepository.listener = this
            mainRepository.initFirebase()

        }
    }

    private fun startServiceWithNotification() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "channel1", "foreground", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)

            val notification = NotificationCompat.Builder(
                this, "channel1"
            ).setSmallIcon(R.mipmap.ic_launcher)

            startForeground(1, notification.build())
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLatestEventReceived(data: DataModel) {
        Log.d(TAG, "onLatestEventReceived: $data")

        if( data.isValid()) {
            when(data.type) {
                DataModelType.StartVideoCall,
                DataModelType.StartAudioCall -> {
                    listener?.onCallReceived(data)
                }
                else -> Unit
            }
        }
    }
}