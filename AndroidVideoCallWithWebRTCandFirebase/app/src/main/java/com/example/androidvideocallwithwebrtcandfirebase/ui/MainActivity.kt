package com.example.androidvideocallwithwebrtcandfirebase.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidvideocallwithwebrtcandfirebase.R
import com.example.androidvideocallwithwebrtcandfirebase.adapter.MainRecyclerViewAdapter
import com.example.androidvideocallwithwebrtcandfirebase.databinding.ActivityMainBinding
import com.example.androidvideocallwithwebrtcandfirebase.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainRecyclerViewAdapter.Listener {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private var username: String? = null

    @Inject lateinit var mainRepository: MainRepository
    private var mainAdapter: MainRecyclerViewAdapter? = null

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
        subscribeObservers()
        //2. start foreground service to listen negotiations and calls
        startMyService()

    }

    private fun subscribeObservers() {
        setupRecycleView()
        mainRepository.observeUserStatus {
            Log.d(TAG, "subscribeObservers: $it")
            mainAdapter?.updateList(it)
        }
    }

    private fun setupRecycleView() {
        mainAdapter = MainRecyclerViewAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = mainAdapter
        }
    }

    private fun startMyService() {

    }

    override fun onVideoCallClicked(username: String) {
        TODO("Not yet implemented")
    }

    override fun onAudioCallClicked(username: String) {
        TODO("Not yet implemented")
    }
}