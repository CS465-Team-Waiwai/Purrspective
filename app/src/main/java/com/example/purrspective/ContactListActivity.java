package com.example.purrspective;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(R.string.contact_header);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        RecyclerView recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("Contact A", R.drawable.default_avatar));
        contacts.add(new Contact("Contact B", R.drawable.default_avatar));
        contacts.add(new Contact("Contact C", R.drawable.default_avatar));

        recyclerView.setAdapter(new ContactAdapter(contacts));

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(v ->
                Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show());

    }
}
