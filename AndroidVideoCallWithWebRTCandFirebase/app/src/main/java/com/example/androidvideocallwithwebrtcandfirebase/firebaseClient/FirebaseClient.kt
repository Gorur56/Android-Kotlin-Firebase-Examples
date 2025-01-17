package com.example.androidvideocallwithwebrtcandfirebase.firebaseClient

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import javax.inject.Inject

class FirebaseClient @Inject constructor(
    private val dbRef: DatabaseReference,
    private val gson: Gson
){
    fun login(userName: String, password: String, done: (Boolean, String?) -> Unit) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //if the current user exists
                if(snapshot.hasChild(userName)) {
                    //user existsi its tşme to check the password
                    val dbPassword = snapshot.child(userName).child("password").value
                } else {
                    //user does not exist, register the user

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}