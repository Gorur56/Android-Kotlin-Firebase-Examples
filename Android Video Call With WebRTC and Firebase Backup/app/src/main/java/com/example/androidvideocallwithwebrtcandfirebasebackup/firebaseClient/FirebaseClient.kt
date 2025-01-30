package com.example.androidvideocallwithwebrtcandfirebasebackup.firebaseClient

import com.example.androidvideocallwithwebrtcandfirebasebackup.utils.FirebaseFieldNames.PASSWORD
import com.example.androidvideocallwithwebrtcandfirebasebackup.utils.FirebaseFieldNames.STATUS
import com.example.androidvideocallwithwebrtcandfirebasebackup.utils.MyEventListener
import com.example.androidvideocallwithwebrtcandfirebasebackup.utils.UserStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val dbRef: DatabaseReference,
    private val gson: Gson
){

    private var currentUsername: String? = null

    private fun setUsername(username: String) {
        this.currentUsername = username
    }

    fun login(username: String, password: String, done: (Boolean, String?) -> Unit) {
        dbRef.addListenerForSingleValueEvent(object : MyEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                //if the current user exists
                if( snapshot.hasChild(username)) {
                    //user exists, its time to check the password
                    val dbPassword = snapshot.child(username).child(PASSWORD).value
                    if(password == dbPassword) {
                        //Password is correct and sign in
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUsername(username)
                                done(true, null)

                            }.addOnFailureListener {
                                done(false, "${it.message}")
                            }
                    } else {
                        //Password is wrong, notify user
                        done(false, "Password is wrong")
                    }
                } else {
                    // user does not exist, register the user
                    dbRef.child(username).child(PASSWORD).setValue(password).addOnCompleteListener {
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUsername(username)
                                done(true, null)
                            }.addOnFailureListener {
                                done(false, it.message)
                            }
                    }.addOnFailureListener {

                    }

                }
            }
        })
    }

    fun observeUsersStatus(status: (List<Pair<String, String>>) -> Unit) {
        dbRef.addValueEventListener(object : MyEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.filter { it.key != currentUsername }.map {
                    it.key!! to it.child(STATUS).value.toString()
                }
                status(list)
            }

        })
    }
}