package com.example.androidvideocallwithwebrtcandfirebasebackup.repository

import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModel

interface MainRepositoryListener {
    fun onLatestEventReceived( data : DataModel )
    fun endCall()
}