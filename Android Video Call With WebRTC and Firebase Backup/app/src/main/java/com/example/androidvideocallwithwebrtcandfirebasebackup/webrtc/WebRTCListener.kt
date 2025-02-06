package com.example.androidvideocallwithwebrtcandfirebasebackup.webrtc

import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModel

interface WebRTCListener {
    fun onTransferEventSocket(data: DataModel)
}