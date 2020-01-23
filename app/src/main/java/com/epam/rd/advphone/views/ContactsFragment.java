package com.epam.rd.advphone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.adapters.ContactRecyclerViewAdapter;
import com.epam.rd.advphone.viewmodels.ContactsViewModel;

import java.util.Objects;

public class ContactsFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.contacts_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //init recyclerView for all contacts
        RecyclerView contactRecyclerView = view.findViewById(R.id.contactRecyclerView);
        contactRecyclerView.setHasFixedSize(true);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ContactsViewModel contactsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ContactsViewModel.class);

        ContactRecyclerViewAdapter adapter = new ContactRecyclerViewAdapter(contactsViewModel);

        contactsViewModel.getContactsList().observe(this, adapter::setContacts);
        contactsViewModel.getCountOfFavourite().observe(this, adapter::setCountOfFavourite);

        contactRecyclerView.setAdapter(adapter);
    }
}
