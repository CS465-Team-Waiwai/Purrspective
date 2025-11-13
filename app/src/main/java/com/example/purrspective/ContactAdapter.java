package com.example.purrspective;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contactList;

    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
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
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView textName;

        ContactViewHolder(View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
        }
    }

}
