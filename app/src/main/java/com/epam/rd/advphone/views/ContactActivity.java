package com.epam.rd.advphone.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.databinding.ActivityContactBinding;
import com.epam.rd.advphone.models.Contact;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import static com.epam.rd.advphone.Constants.CONTACT;

public class ContactActivity extends AppCompatActivity {
    private ActivityContactBinding contactBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact);

        Contact contact = getIntent().getParcelableExtra(CONTACT);
        if (contact != null) {
            contactBinding.setContact(contact);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        hideKeypad();
        switch (item.getItemId()) {
            case R.id.contactMenu:
                if (validateData()) {
                    saveContact();
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideKeypad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(contactBinding.getRoot().getWindowToken(), 0);
        }
    }

    private boolean validateData() {
        if (contactBinding.contactName.getText().length() == 0) {
            showSnackBar(R.string.empty_name);
            return false;
        } else if (contactBinding.contactPhone.getText().length() == 0) {
            showSnackBar(R.string.empty_phone);
            return false;
        }
        return true;
    }

    private void showSnackBar(int resId) {
        Snackbar.make(contactBinding.getRoot(), resId, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    private void saveContact() {
        Contact contact = contactBinding.getContact();

        if (contact == null) {
            contact = new Contact();
        }
        contact.setName(contactBinding.contactName.getText().toString());
        contact.setPhone(contactBinding.contactPhone.getText().toString());
        contact.setEmail(contactBinding.contactEmail.getText().toString());

        Intent intent = new Intent();
        intent.putExtra(CONTACT, contact);
        setResult(RESULT_OK, intent);
        finish();
    }
}