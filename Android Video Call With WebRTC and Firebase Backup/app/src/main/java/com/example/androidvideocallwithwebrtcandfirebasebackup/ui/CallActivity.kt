package com.example.androidvideocallwithwebrtcandfirebasebackup.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidvideocallwithwebrtcandfirebasebackup.R
import com.example.androidvideocallwithwebrtcandfirebasebackup.databinding.ActivityCallBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallActivity : AppCompatActivity() {
    private lateinit var views: ActivityCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        views = ActivityCallBinding.inflate(layoutInflater)
        setContentView(views.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}