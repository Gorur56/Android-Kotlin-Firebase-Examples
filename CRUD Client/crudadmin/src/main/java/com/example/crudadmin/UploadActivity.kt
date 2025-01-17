package com.example.crudadmin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crudadmin.databinding.ActivityUploadBinding
import com.example.crudadmin.entity.UserData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonSave.setOnClickListener {
            val name = binding.edittextUploadName.text.toString()
            val operator = binding.edittextUploadOperator.text.toString()
            val location = binding.edittextUploadLocation.text.toString()
            val phone = binding.edittextUploadPhone.text.toString()

            databaseReference = FirebaseDatabase.getInstance().getReference("Phone_Directory") //Table name

            val users = UserData(name, operator, location, phone)

            databaseReference.child(phone).setValue(users).addOnSuccessListener {
                binding.edittextUploadName.text.clear()
                binding.edittextUploadOperator.text.clear()
                binding.edittextUploadLocation.text.clear()
                binding.edittextUploadPhone.text.clear()

                Snackbar.make(binding.root, "Saved", Snackbar.LENGTH_SHORT).show()

                val intent = Intent( this@UploadActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Snackbar.make(binding.root, "Failed", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}