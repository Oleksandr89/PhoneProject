package com.epam.rd.advphone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.util.ContactBackground;
import com.epam.rd.advphone.util.ContactCommunicator;

import java.util.ArrayList;
import java.util.List;

public class ContactsByNumberRecyclerViewAdapter
        extends RecyclerView.Adapter<ContactsByNumberRecyclerViewAdapter.ContactsByNumberHolder>
        implements ContactCommunicator {

    private List<Contact> contacts;

    public ContactsByNumberRecyclerViewAdapter() {
        contacts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ContactsByNumberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_contact_item, parent, false);
        return new ContactsByNumberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsByNumberHolder holder, int position) {
        Contact contact = contacts.get(position);

        holder.itemView.setOnClickListener(v -> call(v, contact.getPhone()));

        TextView contactIcon = holder.contactIcon;
        ContactBackground.setContactIcon(contactIcon, contact.getName());
        holder.contactName.setText(contact.getName());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts.clear();
        this.contacts.addAll(contacts);
        notifyDataSetChanged();
    }

    class ContactsByNumberHolder extends RecyclerView.ViewHolder {
        final TextView contactIcon;
        final TextView contactName;

        ContactsByNumberHolder(@NonNull View itemView) {
            super(itemView);
            contactIcon = itemView.findViewById(R.id.contactIcon);
            contactName = itemView.findViewById(R.id.contactName);
        }
    }
}
