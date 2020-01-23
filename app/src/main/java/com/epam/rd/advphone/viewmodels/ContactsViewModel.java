package com.epam.rd.advphone.viewmodels;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.epam.rd.advphone.ContactDaoInjection;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.database.ContactsDao;
import com.epam.rd.advphone.models.Contact;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactsViewModel extends AndroidViewModel {
    private final ContactsDao contactsDao;
    private LiveData<Integer> countOfFavourite;
    private LiveData<List<Contact>> contactsList;
    private final MutableLiveData<String> contactsFilteringText = new MutableLiveData<>();
    private final ExecutorService executorService;

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        contactsDao = ContactDaoInjection.provideContactsDao(application);
        executorService = Executors.newSingleThreadExecutor();
        initData();
    }

    private void initData() {
        if (contactsList == null) {
            contactsList = contactsDao.getFavAndAllContacts();
            countOfFavourite = contactsDao.getCountOfFavourites();
        }
    }

    public void setContactsFilteringText(String newText) {
        contactsFilteringText.setValue(newText);
    }

    public LiveData<List<Contact>> getContactsList() {
        return contactsList;
    }

    public LiveData<List<Contact>> getFoundContacts() {
        return Transformations.switchMap(contactsFilteringText, contactsDao::getContactsByName);
    }

    public LiveData<Integer> getCountOfFavourite() {
        return countOfFavourite;
    }

    public void updateContact(Contact contact) {
        executorService.execute(() -> contactsDao.updateContact(contact));
    }

    public void deleteContact(int contactId) {
        executorService.execute(() -> contactsDao.deleteContact(contactId));
    }

    public void insertContact(Contact contact) {
        executorService.execute(() -> contactsDao.insertContact(contact));
    }

    public void showDeleteContactDialog(Contact contact, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setIcon(R.drawable.delete_alert);
        builder.setTitle(R.string.remove_contact);
        builder.setMessage(contact.getName() + "\n\n" + contact.getPhone());
        builder.setPositiveButton(R.string.ok_btn, (dialogInterface, i) -> deleteContact(contact.getId()));
        builder.setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }
}
