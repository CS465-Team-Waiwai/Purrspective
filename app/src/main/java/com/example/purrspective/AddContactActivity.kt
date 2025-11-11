package com.example.purrspective

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class AddContactActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val addBtn: Button = findViewById(R.id.btnAddContact)
        addBtn.setOnClickListener {
            Toast.makeText(this, "Contact added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
