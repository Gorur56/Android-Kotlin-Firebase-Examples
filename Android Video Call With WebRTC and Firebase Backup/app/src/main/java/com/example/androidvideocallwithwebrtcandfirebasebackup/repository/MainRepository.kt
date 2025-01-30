package com.example.androidvideocallwithwebrtcandfirebasebackup.repository

import com.example.androidvideocallwithwebrtcandfirebasebackup.firebaseClient.FirebaseClient
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseClient: FirebaseClient
){
    fun login( username: String, password: String, isDone: (Boolean,String?) -> Unit) {
        firebaseClient.login(username, password, isDone)
    }
}