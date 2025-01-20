package com.example.androidvideocallwithwebrtcandfirebase.ui

import android.content.Intent
import android.os.Bundle
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
    private lateinit var binding: ActivityLoginBinding
    @Inject lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init() {
        binding.apply {
            buttonSignIn.setOnClickListener {
                mainRepository.login(
                    binding.edittextUsername.text.toString(), binding.edittextPassword.text.toString()
                ){ isDone, reason ->
                    if(!isDone) {
                        Snackbar.make(binding.root, reason?: "Unknown error ", Snackbar.LENGTH_SHORT).show()
                    } else {
                        //start moving to mainActivity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("username", binding.edittextUsername.text.toString())
                        })
                    }
                }
            }
        }
    }
}