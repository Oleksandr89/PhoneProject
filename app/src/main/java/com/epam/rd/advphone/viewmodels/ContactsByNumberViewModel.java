package com.epam.rd.advphone.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.epam.rd.advphone.ContactDaoInjection;
import com.epam.rd.advphone.models.Contact;

import java.util.List;

public class ContactsByNumberViewModel extends AndroidViewModel {
    private final LiveData<List<Contact>> contactsLive;
    private final MutableLiveData<String> contactNumber;

    public ContactsByNumberViewModel(@NonNull Application application) {
        super(application);
        contactNumber = new MutableLiveData<>();
        contactsLive = Transformations.switchMap(contactNumber, ContactDaoInjection.provideContactsDao(application)::getContactsByNumber);
    }

    public void setContactNumber(String number) {
        contactNumber.setValue(number);
    }

    public LiveData<List<Contact>> getFoundContacts() {
        return contactsLive;
    }
}