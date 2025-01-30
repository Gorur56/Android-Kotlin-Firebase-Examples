package com.example.androidvideocallwithwebrtcandfirebasebackup.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidvideocallwithwebrtcandfirebasebackup.R
import com.example.androidvideocallwithwebrtcandfirebasebackup.adapter.AdapterListener
import com.example.androidvideocallwithwebrtcandfirebasebackup.adapter.MainRecycleViewAdapter
import com.example.androidvideocallwithwebrtcandfirebasebackup.databinding.ActivityMainBinding
import com.example.androidvideocallwithwebrtcandfirebasebackup.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterListener {
    private val TAG = "MainActivity"
    private lateinit var views: ActivityMainBinding
    private var username: String ?= null

    @Inject lateinit var mainRepository: MainRepository
    private var mainAdapter: MainRecycleViewAdapter ?= null

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

    private fun setupRecycleView() {
        mainAdapter = MainRecycleViewAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        views.mainRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = mainAdapter
        }

    }

    private fun subscribeObservers() {
        setupRecycleView()
        mainRepository.observeUsersStatus{
            Log.d(TAG, "subscribeObservers: $it")
            mainAdapter?.updateList(it)
        }
    }

    private fun startService() {

    }

    override fun onVideoCallClicked(username: String) {

    }

    override fun onAudioCallClicked(username: String) {

    }
}