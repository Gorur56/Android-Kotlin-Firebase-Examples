package com.example.crudclient

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crudclient.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonSearch.setOnClickListener {
            val searchPhone: String = binding.edittextSearchPhone.text.toString()
            if(searchPhone.isNotEmpty()) {
                readData(searchPhone)
            } else {
                Snackbar.make(binding.root, "Please Enter the phone number", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun readData(phone: String) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Phone_Directory")
        databaseReference.child(phone).get().addOnSuccessListener {
            if(it.exists()) {
                val name = it.child("name").value
                val operator = it.child("operator").value
                val location = it.child("location").value
                Snackbar.make(binding.root, "Result Found", Snackbar.LENGTH_SHORT).show()

                binding.edittextSearchPhone.text.clear()

                binding.textviewReadName.text = name.toString()
                binding.textviewreadOperator.text = operator.toString()
                binding.textviewreadLocation.text = location.toString()

            } else {
                Snackbar.make(binding.root, "Phone number does not exist",Snackbar.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Snackbar.make(binding.root,"Something went wrong", Snackbar.LENGTH_SHORT).show()
        }
    }
}