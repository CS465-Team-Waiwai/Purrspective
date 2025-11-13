package com.example.purrspective;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contactList;
    private Context context;

    public ContactAdapter(Context context,List<Contact> contactList) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.textName.setText(contact.getName());
        holder.imageProfile.setImageResource(contact.getImageResId());

        /* TODO: Edit below to open your respective activity when clicking on the contact
        *   toast message is just placeholder, get rid of them when adding your code */
        // click on the row -> open chat window activity
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context,"Open chat window", Toast.LENGTH_SHORT).show();
        });

        // Settings icon click -> open customization page
        holder.settingsDots.setOnClickListener(v -> {
            Toast.makeText(context,"Open customization window", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile, settingsDots;
        TextView textName;

        ContactViewHolder(View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            settingsDots = itemView.findViewById(R.id.contact_settings);
        }
    }

}
