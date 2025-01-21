package com.example.androidvideocallwithwebrtcandfirebase.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidvideocallwithwebrtcandfirebase.R
import com.example.androidvideocallwithwebrtcandfirebase.databinding.ActivityMainBinding
import com.example.androidvideocallwithwebrtcandfirebase.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private var username: String? = null

    @Inject lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
    }

    private fun init() {
        username = intent.getStringExtra("username")
        if (username == null) finish()

       // 1. observe other users status
        subsribeObservers()
        //2. start foreground service to listen negotiations and calls
        startMyService()

    }

    private fun subsribeObservers() {
        mainRepository.observeUserStatus {
            Log.d(TAG, "subsribeObverservers: $it")
        }

    }

    private fun startMyService() {

    }
}