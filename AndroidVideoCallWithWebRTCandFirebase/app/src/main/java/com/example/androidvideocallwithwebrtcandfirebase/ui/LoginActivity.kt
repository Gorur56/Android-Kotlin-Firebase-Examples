package com.example.androidvideocallwithwebrtcandfirebase.ui

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidvideocallwithwebrtcandfirebase.R
import com.example.androidvideocallwithwebrtcandfirebase.databinding.ActivityLoginBinding
import com.example.androidvideocallwithwebrtcandfirebase.repository.MainRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var  views:ActivityLoginBinding
    @Inject lateinit var mainRepository: MainRepository

    private val REQUEST_CODE_SCREEN_CAPTURE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun requestScreenCapturePermission() {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE)
    }



    private fun init(){
        requestScreenCapturePermission()

        views.apply {
            btn.setOnClickListener {
                mainRepository.login(
                    usernameEt.text.toString(),passwordEt.text.toString()
                ){ isDone, reason ->
                    if (!isDone){
                        Toast.makeText(this@LoginActivity, reason, Toast.LENGTH_SHORT).show()
                    }else{
                        //start moving to our main activity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("username",usernameEt.text.toString())
                        })
                    }
                }
            }
        }
    }
}