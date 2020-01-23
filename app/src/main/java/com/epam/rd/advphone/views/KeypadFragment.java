package com.epam.rd.advphone.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.adapters.ContactsByNumberRecyclerViewAdapter;
import com.epam.rd.advphone.databinding.KeypadFragmentBinding;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.viewmodels.ContactsByNumberViewModel;

import java.util.Objects;

import static com.epam.rd.advphone.Constants.CONTACT;
import static com.epam.rd.advphone.RequestCodes.REQUEST_NEW_CONTACT;

public class KeypadFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, ContactCommunicator {

    private KeypadFragmentBinding binding;
    private ContactsByNumberRecyclerViewAdapter adapter;
    private ContactsByNumberViewModel contactsByNumberViewModel;
    private EditText phoneNumber;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.keypad_fragment, container, false);
        binding.setKeypad(this);
        phoneNumber = binding.phoneNumber;
        binding.newContactBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this.getActivity(), ContactActivity.class);
            if (phoneNumber.getText().length() > 0) {
                Contact contact = new Contact();
                contact.setPhone(phoneNumber.getText().toString());
                intent.putExtra(CONTACT, contact);
            }
            ((Activity)view.getContext()).startActivityForResult(intent, REQUEST_NEW_CONTACT);
        });

        phoneNumber.setShowSoftInputOnFocus(false);

        setOnclickListeners();
        initContactByNumberRecyclerView();
        setupContactsByNumberViewModel();
        initPhoneNumberListener();
        return binding.getRoot();
    }

    public void call() {
        call(binding.getRoot(), phoneNumber.getText().toString());
    }

    private void initPhoneNumberListener() {
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                contactsByNumberViewModel.setContactNumber(s.length() > 0 ? "%" + s.toString() + "%" : "");
            }
        });
    }

    private void setupContactsByNumberViewModel() {
        contactsByNumberViewModel = ViewModelProviders.of(this).get(ContactsByNumberViewModel.class);
        contactsByNumberViewModel.getFoundContacts().observe(this, contacts -> {
            binding.newContactBtn.setVisibility((contacts.isEmpty() && phoneNumber.getText().length() > 0) ? View.VISIBLE : View.GONE);
            adapter.setContacts(contacts);
        });
    }

    private void initContactByNumberRecyclerView() {
        RecyclerView contactByNumberRecyclerView = binding.contactByNumberRecyclerView;
        contactByNumberRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        contactByNumberRecyclerView.setLayoutManager(manager);
        DividerItemDecoration decoration = new DividerItemDecoration(Objects.requireNonNull(getContext()), manager.getOrientation());
        contactByNumberRecyclerView.addItemDecoration(decoration);
        adapter = new ContactsByNumberRecyclerViewAdapter();
        contactByNumberRecyclerView.setAdapter(adapter);
    }

    private void setOnclickListeners() {
        binding.buttonNumberOne.setOnClickListener(this);
        binding.buttonNumberTwo.setOnClickListener(this);
        binding.buttonNumberThree.setOnClickListener(this);
        binding.buttonNumberFour.setOnClickListener(this);
        binding.buttonNumberFive.setOnClickListener(this);
        binding.buttonNumberSix.setOnClickListener(this);
        binding.buttonNumberSeven.setOnClickListener(this);
        binding.buttonNumberEight.setOnClickListener(this);
        binding.buttonNumberNine.setOnClickListener(this);
        binding.buttonNumberZero.setOnClickListener(this);
        binding.buttonAsterisk.setOnClickListener(this);
        binding.buttonSharp.setOnClickListener(this);
        binding.imageButtonBackspace.setOnClickListener(this);
        binding.buttonNumberZero.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageButton_backspace) {
            String textDelete = phoneNumber.getText().toString();
            int positionDelete = phoneNumber.getSelectionEnd();

            if (textDelete.length() > 0) {

                if (positionDelete != 0) {
                    String beforeCursor = textDelete.substring(0, positionDelete - 1);
                    String afterCursor = textDelete.substring(positionDelete);
                    String enteredString = beforeCursor + afterCursor;
                    phoneNumber.setText(enteredString);
                    phoneNumber.setSelection(positionDelete - 1);
                } else if (!phoneNumber.isFocused()) {
                    phoneNumber.setText(textDelete.substring(0, textDelete.length() - 1));
                }
            }
        } else {
            String textInsert = phoneNumber.getText().toString();
            int positionInsert = phoneNumber.getSelectionEnd();

            if (!phoneNumber.isFocused()) {
                phoneNumber.setText(phoneNumber.getText() + view.getTag().toString());
                phoneNumber.setSelection(phoneNumber.getText().length());
            } else {
                phoneNumber.setText(textInsert.substring(0, positionInsert) +
                        view.getTag().toString() + textInsert.substring(positionInsert));
                phoneNumber.setSelection(positionInsert + 1);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.button_number_zero) {
            phoneNumber.setText("+");
        }
        return true;
    }
}
