package com.example.crudadmin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crudadmin.databinding.ActivityUpdateBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonUpdate.setOnClickListener {
            val referencePhone = binding.edittextUpdateReferencePhone.text.toString()
            val updateName = binding.edittextUpdateName.text.toString()
            val updateOperator = binding.edittextUpdateOperator.text.toString()
            val updateLocation = binding.edittextUpdateLocation.text.toString()

            updateData(referencePhone, updateName, updateOperator, updateLocation)
        }
    }

    private fun updateData(phone: String, name: String, operator: String, location: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Phone_Directory")
        val user = mapOf<String, String>("name" to name, "operator" to operator, "location" to location)

        databaseReference.child(phone).updateChildren(user).addOnSuccessListener {
            binding.edittextUpdateReferencePhone.text.clear()
            binding.edittextUpdateName.text.clear()
            binding.edittextUpdateOperator.text.clear()
            binding.edittextUpdateLocation.text.clear()
            Snackbar.make(binding.root, "updated", Snackbar.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Snackbar.make(binding.root, "Unable to update", Snackbar.LENGTH_SHORT).show()
        }
    }
}