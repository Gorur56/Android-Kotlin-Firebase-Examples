package com.example.androidvideocallwithwebrtcandfirebasebackup.repository

import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModel
import com.example.androidvideocallwithwebrtcandfirebasebackup.firebaseClient.FirebaseClient
import com.example.androidvideocallwithwebrtcandfirebasebackup.firebaseClient.FirebaseClientListener
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseClient: FirebaseClient
){

    var listener: MainRepositoryListener ?= null

    fun login( username: String, password: String, isDone: (Boolean,String?) -> Unit) {
        firebaseClient.login(username, password, isDone)
    }

    fun observeUsersStatus( status: (List<Pair<String, String>>) -> Unit) {
        firebaseClient.observeUsersStatus(status)
    }

    fun initFirebase() {
        firebaseClient.subscribeForLatestEvent(object : FirebaseClientListener {
            override fun onLatestEventReceived(event: DataModel) {
                listener?.onLatestEventReceived(event)

                when(event.type){
                    else -> Unit
                }
            }

        })
    }
}