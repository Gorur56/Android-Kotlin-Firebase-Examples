package com.example.androidvideocallwithwebrtcandfirebasebackup.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidvideocallwithwebrtcandfirebasebackup.R
import com.example.androidvideocallwithwebrtcandfirebasebackup.databinding.ActivityMainBinding
import com.example.androidvideocallwithwebrtcandfirebasebackup.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var views: ActivityMainBinding
    private var username: String ?= null

    @Inject lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)

        init()
    }

    private fun init() {
        username = intent.getStringExtra("username")
        if (username == null) finish()

        //1. observe other users status
        subscribeObservers()

        //2. start foreground service  to listen negotiations and
        startService()
    }

    private fun subscribeObservers() {
        mainRepository.observeUsersStatus{
            Log.d(TAG, "subscribeObservers: $it")
        }
    }

    private fun startService() {

    }
}