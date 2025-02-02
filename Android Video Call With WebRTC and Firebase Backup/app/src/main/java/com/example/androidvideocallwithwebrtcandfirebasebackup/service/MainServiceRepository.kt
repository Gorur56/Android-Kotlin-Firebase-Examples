package com.example.androidvideocallwithwebrtcandfirebasebackup.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import javax.inject.Inject

class MainServiceRepository @Inject constructor(
    private val context: Context
) {

    fun startService(username: String) {
        Thread {
            val intent = Intent(context, MainService::class.java).apply {
                putExtra("username", username)
                action = MainServiceActions.START_SERVICE.name
            }

            startServiceIntent(intent)
        }.start()
    }



    private fun startServiceIntent(intent: Intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34)
                if (ContextCompat.checkSelfPermission(
                        context,
                        "android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION"
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    context.startForegroundService(intent)
                } else {
                    Log.e("MainServiceRepository", "Gerekli izinler verilmemiş!")
                }
            } else {
                context.startForegroundService(intent)
            }
        } catch (e: SecurityException) {
            Log.e("MainServiceRepository", "Foreground Service başlatılırken hata oluştu: ${e.message}")
        }
    }
}