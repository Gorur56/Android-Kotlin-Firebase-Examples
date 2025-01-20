package com.example.androidvideocallwithwebrtcandfirebase.firebaseClient

import android.util.Log
import com.example.androidvideocallwithwebrtcandfirebase.utils.FirebaseFieldNames.PASSWORD
import com.example.androidvideocallwithwebrtcandfirebase.utils.FirebaseFieldNames.STATUS
import com.example.androidvideocallwithwebrtcandfirebase.utils.UserStatus
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

    private var currentUserName: String? = null

    private fun setUserName(userName: String) {
        this.currentUserName = userName
    }

    fun login(userName: String, password: String, done: (Boolean, String?) -> Unit) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //if the current user exists
                if(snapshot.hasChild(userName)) {
                    //user existsi its tşme to check the password
                    val dbPassword = snapshot.child(userName).child(PASSWORD).value
                    if ( password == dbPassword) {
                        //password is correct and sign in
                        dbRef.child(userName).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUserName(userName)
                                done(true,null)
                            }.addOnFailureListener {
                                done(false,"${it.message}")
                            }
                    } else {
                        //password is wrong, notify user
                        done(false, "Password is wrong")
                    }
                } else {
                    //user does not exist, register the user
                    dbRef.child(userName).child(PASSWORD).setValue(password).addOnCompleteListener {
                        dbRef.child(userName).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUserName(userName)
                                done(true, null)
                            }.addOnFailureListener {
                                done(false, it.message)
                            }
                    }.addOnFailureListener {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseClient", "Database operation was cancelled: ${error.message}")

            }

        })
    }
}