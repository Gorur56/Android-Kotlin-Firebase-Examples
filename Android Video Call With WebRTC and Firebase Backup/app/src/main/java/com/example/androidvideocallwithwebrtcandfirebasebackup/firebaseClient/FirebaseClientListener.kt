package com.example.androidvideocallwithwebrtcandfirebasebackup.firebaseClient

import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModel

interface FirebaseClientListener {
    fun onLatestEventReceived( event: DataModel )
}