package com.epam.rd.advphone.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.ContactDaoInjection;
import com.epam.rd.advphone.SmsDaoInjection;
import com.epam.rd.advphone.database.SmsDao;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.models.Sms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PickContactViewModel extends AndroidViewModel {
    private final SmsDao smsDao;
    private final LiveData<List<Contact>> contactsLive;
    private final List<Contact> contactsList;
    private final List<Contact> tempContactsList;
    private final MutableLiveData<String> pattern;

    public PickContactViewModel(@NonNull Application application) {
        super(application);
        smsDao = SmsDaoInjection.provideSmsDao(application);
        contactsList = new ArrayList<>();
        tempContactsList = new ArrayList<>();
        contactsLive = ContactDaoInjection.provideContactsDao(application).getAllContacts();
        pattern = new MutableLiveData<>();
        pattern.setValue("[\\S ]*");
    }

    public List<Contact> getContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return contactsList.stream()
                    .filter(contact -> contact.getName().toLowerCase().matches(Objects.requireNonNull(pattern.getValue())))
                    .collect(Collectors.toList());
        } else {
            tempContactsList.clear();
            for (Contact contact : contactsList) {
                if (contact.getName().toLowerCase().matches(Objects.requireNonNull(pattern.getValue()))) {
                    tempContactsList.add(contact);
                }
            }
            return tempContactsList;
        }
    }

    public void setPattern(String pattern) {
        this.pattern.postValue("[\\S ]*" + pattern.toLowerCase() + "[\\S ]*");
    }

    public MutableLiveData<String> getPattern() {
        return pattern;
    }

    public LiveData<List<Contact>> getContactsLive() {
        return contactsLive;
    }

    public void setContactsList(List<Contact> contactsList) {
        this.contactsList.clear();
        this.contactsList.addAll(contactsList);
    }

    public void insertNewSms(Sms sms) {
        Executors.newSingleThreadExecutor().execute(() -> smsDao.insertSms(sms));
    }

    public void contactPicked(Context context, int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CONTACT_NUMBER, contactsList.get(position).getPhone());
        intent.putExtra(Constants.CONTACT_NAME, contactsList.get(position).getName());
        Activity activity = ((Activity) context);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}