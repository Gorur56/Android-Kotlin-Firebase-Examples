package com.example.androidvideocallwithwebrtcandfirebasebackup.adapter

interface AdapterListener {
    fun onVideoCallClicked( username: String )
    fun onAudioCallClicked( username: String )
}