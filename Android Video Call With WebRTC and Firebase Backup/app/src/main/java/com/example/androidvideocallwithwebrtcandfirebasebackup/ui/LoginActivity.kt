package com.example.androidvideocallwithwebrtcandfirebasebackup.ui

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(views.root)

        init()
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
                    }

                }
            }
        }
    }
}