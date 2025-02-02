package com.example.androidvideocallwithwebrtcandfirebasebackup.ui

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidvideocallwithwebrtcandfirebasebackup.R
import com.example.androidvideocallwithwebrtcandfirebasebackup.databinding.ActivityLoginBinding
import com.example.androidvideocallwithwebrtcandfirebasebackup.repository.MainRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var views: ActivityLoginBinding
    @Inject lateinit var mainRepository: MainRepository

    private val REQUEST_CODE_SCREEN_CAPTURE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(views.root)

        requestScreenCapturePermission()
        init()
    }

    private fun requestScreenCapturePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34)
            requestPermissions(arrayOf("android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION"), REQUEST_CODE_SCREEN_CAPTURE)
        }
    }

    private fun init() {
        views.apply {
            btn.setOnClickListener {
                mainRepository.login(
                    usernameEt.text.toString(), passwordEt.text.toString()
                ){ isDone, reason ->
                    if(!isDone) {
                        Snackbar.make(views.root, reason!!, Snackbar.LENGTH_SHORT).show()
                    } else {
                        //Start moving to our main activity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("username", usernameEt.text.toString())
                        })
                    }

                }
            }
        }
    }
}