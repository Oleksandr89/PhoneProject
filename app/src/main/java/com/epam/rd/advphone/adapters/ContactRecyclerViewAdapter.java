package com.epam.rd.advphone.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.databinding.ContactItemBinding;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.util.ContactBackground;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.util.OnContactEditClickListener;
import com.epam.rd.advphone.viewmodels.ContactsViewModel;
import com.epam.rd.advphone.views.ContactActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactRecyclerViewAdapter
        extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>
        implements ContactCommunicator, OnContactEditClickListener {

    private RecyclerView recyclerView;
    private final ContactsViewModel viewModel;
    private List<Contact> contacts;
    private int prev_expanded = -1;
    private int countOfFavourite;

    public ContactRecyclerViewAdapter(ContactsViewModel viewModel) {
        this.contacts = new ArrayList<>();
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ContactItemBinding contactItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.contact_item, parent, false);
        contactItemBinding.setContactCommunicator(this);
        contactItemBinding.setOnContactEditClickListener(this);

        contactItemBinding.getRoot().setOnLongClickListener(view -> {
            ViewModelProviders.of((FragmentActivity) parent.getContext()).get(ContactsViewModel.class).showDeleteContactDialog(contactItemBinding.getContact(), view);
            return true;
        });

        return new ContactViewHolder(contactItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        //if previous item is expanded and user scroll recyclerView
        //need setup visibility to GONE
        if (Objects.requireNonNull(holder.contactItemBinding).contactDetailInfoContainer.getVisibility() == View.VISIBLE) {
            holder.contactItemBinding.contactDetailInfoContainer.setVisibility(View.GONE);
        }

        //set background for favourite contacts
        setFavouriteItemsBackground(holder, position);

        Contact contact = contacts.get(position);
        holder.contactItemBinding.setContact(contact);
        holder.contactItemBinding.setView(recyclerView);
        holder.contactItemBinding.setItemPosition(position);

        if (contact.getContactImage() != null) {
            holder.contactItemBinding.contactImage.setImageURI(Uri.parse(contact.getContactImage()));
        } else {
            CircleImageView circleView = holder.contactItemBinding.contactImage;
            circleView.setCircleBackgroundColor(ContactBackground.getColor(recyclerView.getContext(), contact.getName().charAt(0)));
            circleView.setImageResource(R.drawable.account);
        }

        holder.contactItemBinding.getRoot().setOnClickListener(view -> {
            final boolean visibility = holder.contactItemBinding.contactDetailInfoContainer.getVisibility() == View.VISIBLE;

            if (!visibility) {
                holder.itemView.setActivated(true);
                holder.contactItemBinding.contactDetailInfoContainer.setVisibility(View.VISIBLE);

                if (prev_expanded != -1 && prev_expanded != position) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(prev_expanded);
                    if (viewHolder != null) {
                        viewHolder.itemView.setActivated(false);
                        viewHolder.itemView.findViewById(R.id.contactDetailInfoContainer).setVisibility(View.GONE);
                    }
                }

                prev_expanded = position;
            } else {
                holder.itemView.setActivated(false);
                holder.contactItemBinding.contactDetailInfoContainer.setVisibility(View.GONE);
            }
            TransitionManager.beginDelayedTransition(recyclerView);
        });
    }

    public void setCountOfFavourite(int countOfFavourite) {
        this.countOfFavourite = countOfFavourite;
    }

    private void setFavouriteItemsBackground(@NonNull ContactViewHolder holder, int position) {
        if (countOfFavourite > 0) {
            Objects.requireNonNull(holder.contactItemBinding).mainContactInfoContainer.setBackgroundColor((position < countOfFavourite) ?
                    recyclerView.getContext().getResources().getColor(R.color.favourite_contact) : Color.TRANSPARENT);

        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
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

    @Override
    public void onFavouriteClick(int position, Contact contact) {
        contact.setFavourite(!contact.isFavourite());
        viewModel.updateContact(contact);
    }

    @Override
    public void onEditClick(Contact contact) {
        Intent intent = new Intent(recyclerView.getContext(), ContactActivity.class);
        intent.putExtra(Constants.CONTACT, contact);
        ((Activity)recyclerView.getContext()).startActivityForResult(intent, RequestCodes.REQUEST_EDIT_CONTACT);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        final ContactItemBinding contactItemBinding;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactItemBinding = DataBindingUtil.bind(itemView);
        }
    }
}
