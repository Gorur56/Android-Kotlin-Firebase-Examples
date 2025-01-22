package com.example.androidvideocallwithwebrtcandfirebase.firebaseClient

import android.util.Log
import com.example.androidvideocallwithwebrtcandfirebase.utils.FirebaseFieldNames.PASSWORD
import com.example.androidvideocallwithwebrtcandfirebase.utils.FirebaseFieldNames.STATUS
import com.example.androidvideocallwithwebrtcandfirebase.utils.MyEventListener
import com.example.androidvideocallwithwebrtcandfirebase.utils.UserStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val dbRef: DatabaseReference,
    private val gson: Gson
){

    private var currentUserName: String? = null

    private fun setUserName(userName: String) {
        this.currentUserName = userName
    }

    fun login(username: String, password: String, done: (Boolean, String?) -> Unit) {
        dbRef.addListenerForSingleValueEvent(object  : MyEventListener(){
            override fun onDataChange(snapshot: DataSnapshot) {
                //if the current user exists
                if (snapshot.hasChild(username)){
                    //user exists , its time to check the password
                    val dbPassword = snapshot.child(username).child(PASSWORD).value
                    if (password == dbPassword) {
                        //password is correct and sign in
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUserName(username)
                                done(true,null)
                            }.addOnFailureListener {
                                done(false,"${it.message}")
                            }
                    }else{
                        //password is wrong, notify user
                        done(false,"Password is wrong")
                    }

                }else{
                    //user doesnt exist, register the user
                    dbRef.child(username).child(PASSWORD).setValue(password).addOnCompleteListener {
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUserName(username)
                                done(true,null)
                            }.addOnFailureListener {
                                done(false,it.message)
                            }
                    }.addOnFailureListener {
                        done(false,it.message)
                    }

                }
            }
        })
    }

    fun observeUsersStatus(status: (List<Pair<String, String>>) -> Unit) {
        dbRef.addValueEventListener(object : MyEventListener() {

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    Log.d("observeUserStatus", "Key: ${it.key}, status: ${it.child(STATUS).value}, currentUserName: $currentUserName")
                }

                Log.d("observeUserStatus", "currentUserName: $currentUserName" )
                val list = snapshot.children.filter { it.key != currentUserName }
                    .map { it.key!! to it.child(STATUS).value.toString()
                }

                Log.d("observeUserStatus", "Filtered List: $list")
                status(list)

            }
        })
    }
}