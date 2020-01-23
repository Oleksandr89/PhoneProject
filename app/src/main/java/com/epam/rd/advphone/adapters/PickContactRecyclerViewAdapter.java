package com.epam.rd.advphone.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.util.ContactBackground;
import com.epam.rd.advphone.viewmodels.PickContactViewModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PickContactRecyclerViewAdapter extends RecyclerView.Adapter<PickContactRecyclerViewAdapter.ContactViewHolder> {
    private List<Contact> contacts;

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_contact_item_with_circle_image_view, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            Context context = holder.itemView.getContext();
            ViewModelProviders.of((FragmentActivity) context).get(PickContactViewModel.class).contactPicked(context, position);
        });

        Contact contact = contacts.get(position);
        CircleImageView contactIcon = holder.contactIcon;

        if (contact != null) {
            holder.contactName.setText(contacts.get(position).getName());

            if (contact.getContactImage() != null){
                contactIcon.setImageURI(Uri.parse(contact.getContactImage()));
            } else{
                contactIcon.setCircleBackgroundColor(ContactBackground.getColor(holder.itemView.getContext(), contact.getName().charAt(0)));
                contactIcon.setImageResource(R.drawable.account);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (contacts != null) {
            return contacts.size();
        } else {
            return 0;
        }
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        final CircleImageView contactIcon;
        final TextView contactName;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactIcon = itemView.findViewById(R.id.contactIcon);
            contactName = itemView.findViewById(R.id.contactName);
        }
    }
}
