package com.example.androidvideocallwithwebrtcandfirebasebackup.service

import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModel

interface MainServiceListener {
    fun onCallReceived(model: DataModel)
}