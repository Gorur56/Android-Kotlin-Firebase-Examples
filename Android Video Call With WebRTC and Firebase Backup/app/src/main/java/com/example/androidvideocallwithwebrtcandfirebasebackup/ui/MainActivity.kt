package com.example.androidvideocallwithwebrtcandfirebasebackup.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidvideocallwithwebrtcandfirebasebackup.R
import com.example.androidvideocallwithwebrtcandfirebasebackup.adapter.AdapterListener
import com.example.androidvideocallwithwebrtcandfirebasebackup.adapter.MainRecycleViewAdapter
import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModel
import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModelType
import com.example.androidvideocallwithwebrtcandfirebasebackup.databinding.ActivityMainBinding
import com.example.androidvideocallwithwebrtcandfirebasebackup.repository.MainRepository
import com.example.androidvideocallwithwebrtcandfirebasebackup.service.MainService
import com.example.androidvideocallwithwebrtcandfirebasebackup.service.MainServiceListener
import com.example.androidvideocallwithwebrtcandfirebasebackup.service.MainServiceRepository
import com.example.androidvideocallwithwebrtcandfirebasebackup.utils.getCameraAndMicPermission
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterListener, MainServiceListener {
    private val TAG = "MainActivity"
    private lateinit var views: ActivityMainBinding
    private var username: String ?= null

    @Inject lateinit var mainRepository: MainRepository
    @Inject lateinit var mainServiceRepository: MainServiceRepository

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
        MainService.listener = this

        mainRepository.observeUsersStatus{
            Log.d(TAG, "subscribeObservers: $it")
            mainAdapter?.updateList(it)
        }
    }

    private fun startService() {
        mainServiceRepository.startService(username!!)
    }

    override fun onVideoCallClicked(username: String) {
        // check if permission of mic and camera is taken
        getCameraAndMicPermission {
            mainRepository.sendConnectionRequest(username, true){
                if( it ) {
                    //we have to start video call
                    //We wanna create an intent to move to call activity
                    startActivity(Intent(this, CallActivity::class.java).apply {
                        putExtra("target", username)
                        putExtra("isVideoCall", true)
                        putExtra("isCaller", true)
                    })
                }

            }
        }
    }

    override fun onAudioCallClicked(username: String) {
        getCameraAndMicPermission {
            mainRepository.sendConnectionRequest(username, false) {
                if(it) {
                    // we have to start audio call
                    // we wanna create an intent move to call activity
                    startActivity(Intent(this, CallActivity::class.java).apply {
                        putExtra("target", username)
                        putExtra("isVideoCall", false)
                        putExtra("isCaller", true)
                    })
                }
            }

        }
    }

    override fun onCallReceived(model: DataModel) {
        runOnUiThread {
            views.apply {
                val isVideoCall = model.type == DataModelType.StartVideoCall
                val isVideoCallText = if (isVideoCall) "Video" else "Audio"

                incomingCallTitleTv.text = "${model.sender} is $isVideoCallText Calling you"
                incomingCallLayout.isVisible = true

                acceptButton.setOnClickListener {
                    getCameraAndMicPermission {
                        incomingCallLayout.isVisible = false

                        //create an intent to go to video call activity
                        startActivity(Intent(this@MainActivity, CallActivity::class.java).apply {
                            putExtra("target", model.sender)
                            putExtra("isVideoCall", isVideoCall)
                            putExtra("isCaller", false)
                        })
                    }
                }

                declineButton.setOnClickListener {
                    incomingCallLayout.isVisible = false
                }
            }
        }
    }
}