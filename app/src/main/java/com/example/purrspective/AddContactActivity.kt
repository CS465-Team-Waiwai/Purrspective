package com.example.purrspective

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class AddContactActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val contactName = findViewById<EditText>(R.id.editName)
        val contactPhone = findViewById<EditText>(R.id.editPhone)

        val addBtn: Button = findViewById(R.id.btnAddContact)
        addBtn.setOnClickListener {
            val name = contactName.text.toString()
            val phone = contactPhone.text.toString()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contact = Contact(name, phone, R.drawable.default_avatar)
            val db = AppDatabase.getInstance(this)
            db.contactDao().insert(contact)

            val intent = Intent(this, ContactListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()

        }
    }
}
