package com.example.androidvideocallwithwebrtcandfirebasebackup.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.androidvideocallwithwebrtcandfirebasebackup.R
import com.example.androidvideocallwithwebrtcandfirebasebackup.databinding.ActivityCallBinding
import com.example.androidvideocallwithwebrtcandfirebasebackup.service.EndCallListener
import com.example.androidvideocallwithwebrtcandfirebasebackup.service.MainService
import com.example.androidvideocallwithwebrtcandfirebasebackup.service.MainServiceRepository
import com.example.androidvideocallwithwebrtcandfirebasebackup.webrtc.RTCAudioManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallActivity : AppCompatActivity(), EndCallListener {

    private var target: String ?= null
    private var isVideoCall: Boolean = true
    private var isCaller: Boolean = true

    private var isMicrophoneMuted = false
    private var isCameraMuted = false
    private var isSpeakerMode = true

    @Inject lateinit var serviceRepository: MainServiceRepository

    private lateinit var views: ActivityCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityCallBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init() {
        intent.getStringExtra("target")?.let {
            this.target = it
        }?: kotlin.run {
            finish()
        }

        isVideoCall = intent.getBooleanExtra("isVideoCall", true)
        isCaller = intent.getBooleanExtra("isCaller", true)

        views.apply {
            if(!isVideoCall) {
                toggleCameraButton.isVisible = false
                screenShareButton.isVisible = false
                switchCameraButton.isVisible = false
            }
            MainService.remoteSurfaceView = remoteView
            MainService.localSurfaceView = localView
            serviceRepository.setupViews(isVideoCall, isCaller, target!!)

            endCallButton.setOnClickListener {
                serviceRepository.sendEndCall()
            }

            switchCameraButton.setOnClickListener {
                serviceRepository.switchCamera()
            }
        }
        setupMicToggleClicked()
        setupCameraToggleClicked()
        setupToggleAudioDevice()

        MainService.endCallListener = this
    }

    private fun setupMicToggleClicked(){
        views.apply {
            toggleMicrophoneButton.setOnClickListener {
                if (!isMicrophoneMuted){
                    //we should mute our mic
                    //1. send a command to repository
                    serviceRepository.toggleAudio(true)
                    //2. update ui to mic is muted
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_on)
                }else{
                    //we should set it back to normal
                    //1. send a command to repository to make it back to normal status
                    serviceRepository.toggleAudio(false)
                    //2. update ui
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_off)
                }
                isMicrophoneMuted = !isMicrophoneMuted
            }
        }
    }

    private fun setupToggleAudioDevice() {
        views.apply {
            toggleAudioDevice.setOnClickListener {
                if( isSpeakerMode) {
                    // we should set it to earpiece mode
                    toggleAudioDevice.setImageResource(R.drawable.ic_speaker)
                    // we should send a command to our  service to switch between devices
                    serviceRepository.toggleAudioDevice(RTCAudioManager.AudioDevice.EARPIECE.name)
                } else {
                    // we should set it to speaker mode
                    toggleAudioDevice.setImageResource(R.drawable.ic_ear)
                    serviceRepository.toggleAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE.name)
                }
                isSpeakerMode = !isSpeakerMode
            }
        }
    }


    private fun setupCameraToggleClicked(){
        views.apply {
            toggleCameraButton.setOnClickListener {
                if (!isCameraMuted){
                    serviceRepository.toggleVideo(true)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_on)
                }else{
                    serviceRepository.toggleVideo(false)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_off)
                }

                isCameraMuted = !isCameraMuted
            }
        }
    }

    override fun onCallEnded() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        MainService.remoteSurfaceView?.release()
        MainService.remoteSurfaceView = null

        MainService.localSurfaceView?.release()
        MainService.localSurfaceView = null
    }
}