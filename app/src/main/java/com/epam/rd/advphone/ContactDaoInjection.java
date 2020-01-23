package com.epam.rd.advphone;

import android.content.Context;

import androidx.annotation.NonNull;

import com.epam.rd.advphone.database.ContactsDao;
import com.epam.rd.advphone.database.ContactsDatabase;

public class ContactDaoInjection {
    public static ContactsDao provideContactsDao (@NonNull Context context) {
        return ContactsDatabase.getInstance(context).contactDao();
    }
}
