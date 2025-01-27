package com.example.androidvideocallwithwebrtcandfirebase.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidvideocallwithwebrtcandfirebase.adapter.MainRecyclerViewAdapter
import com.example.androidvideocallwithwebrtcandfirebase.databinding.ActivityMainBinding
import com.example.androidvideocallwithwebrtcandfirebase.repository.MainRepository
import com.example.androidvideocallwithwebrtcandfirebase.service.MainService
import com.example.androidvideocallwithwebrtcandfirebase.service.MainServiceRepository
import com.example.androidvideocallwithwebrtcandfirebase.utils.DataModel
import com.example.androidvideocallwithwebrtcandfirebase.utils.DataModelType
import com.example.androidvideocallwithwebrtcandfirebase.utils.getCameraAndMicPermission
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainRecyclerViewAdapter.Listener, MainService.Listener {
    private val TAG = "MainActivity"

    private lateinit var views: ActivityMainBinding
    private var username: String? = null

    @Inject
    lateinit var mainRepository: MainRepository
    @Inject
    lateinit var mainServiceRepository: MainServiceRepository
    private var mainAdapter: MainRecyclerViewAdapter? = null

    // İzin istek kodları
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val MICROPHONE_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init() {
        username = intent.getStringExtra("username")

        if (username == null) finish()

        // 1. İzinleri kontrol et
        //checkPermissions()

        //1. observe other users status
        subscribeObservers()

        //2. start foreground service to listen negotiations and calls.
        startMyService()
    }

    private fun subscribeObservers() {
        setupRecyclerView()
        MainService.listener = this
        mainRepository.observeUsersStatus {
            Log.d(TAG, "subscribeObservers: $it")
            mainAdapter?.updateList(it)
        }
    }

    private fun setupRecyclerView() {
        mainAdapter = MainRecyclerViewAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        views.mainRecyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = mainAdapter
        }
    }

    private fun startMyService() {
        mainServiceRepository.startService(username!!)
    }

    private fun checkPermissions() {
        // Kamera ve mikrofon izinlerini kontrol et
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

            // Eğer izin verilmemişse, kullanıcıdan izin iste
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        // Kamera ve mikrofon izinlerini istemek
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    // İzinler verildikten sonra onRequestPermissionsResult() metodunda işlem yapılır
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE || requestCode == MICROPHONE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Kullanıcı izin verdi, video aramaya başlayabilirsin
               startMyService()
            } else {
                // Kullanıcı izin vermedi
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onVideoCallClicked(username: String) {
        //check if permission of mic and camera is taken
        getCameraAndMicPermission {
            mainRepository.sendConnectionRequest(username, true) {
                if (it){
                    //we have to start video call
                    //we wanna create an intent to move to call activity
                    startActivity(Intent(this,CallActivity::class.java).apply {
                        putExtra("target",username)
                        putExtra("isVideoCall",true)
                        putExtra("isCaller",true)
                    })

                }
            }

        }
    }

    override fun onAudioCallClicked(username: String) {
        getCameraAndMicPermission {
            mainRepository.sendConnectionRequest(username, false) {
                if (it){
                    //we have to start audio call
                    //we wanna create an intent to move to call activity
                    startActivity(Intent(this,CallActivity::class.java).apply {
                        putExtra("target",username)
                        putExtra("isVideoCall",false)
                        putExtra("isCaller",true)
                    })
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mainServiceRepository.stopService()
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
                        startActivity(Intent(this@MainActivity,CallActivity::class.java).apply {
                            putExtra("target",model.sender)
                            putExtra("isVideoCall",isVideoCall)
                            putExtra("isCaller",false)
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