package com.example.crudadmin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crudadmin.databinding.ActivityDeleteBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonDelete.setOnClickListener {
            val phone = binding.edittextDeletePhone.text.toString()
            if (phone.isNotEmpty()) {
                deleteData(phone)
            } else {
                Snackbar.make(binding.root, "Please enter the phone number", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun deleteData(phone: String){
        databaseReference = FirebaseDatabase.getInstance().getReference("Phone_Directory")
        databaseReference.child(phone).removeValue().addOnSuccessListener {
            binding.edittextDeletePhone.text.clear()
            Snackbar.make(binding.root, "Deleted", Snackbar.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Snackbar.make(binding.root, "Unable to delete", Snackbar.LENGTH_SHORT).show()
        }
    }
}