package com.epam.rd.advphone.repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.epam.rd.advphone.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class PhoneContactsProvider implements ContactsProvider {
    private static PhoneContactsProvider phoneContactsProvider;
    private final ContentResolver contentResolver;

    private PhoneContactsProvider(Context context){
        this.contentResolver = context.getContentResolver();
    }

    public static PhoneContactsProvider getInstance(Context context){
        if (phoneContactsProvider == null) {
            phoneContactsProvider = new PhoneContactsProvider(context);
        }
        return phoneContactsProvider;
    }

    @Override
    public List<Contact> getContacts() {
        List<Contact> tempList = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Email.ADDRESS
        };

        try (Cursor cursor = contentResolver.query(uri, projection, ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
                new String[]{"2"}, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                int contactIdColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int contactTypeColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                int contactNameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int contactPhoneColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int contactImageColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                int contactEmailColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);

                while (cursor.moveToNext()) {
                    tempList.add(new Contact(
                            cursor.getInt(contactIdColumnIndex),
                            cursor.getString(contactImageColumnIndex),
                            cursor.getString(contactNameColumnIndex),
                            cursor.getString(contactPhoneColumnIndex)
                                    .trim()
                                    .replace(" ", ""),
                            cursor.getString(contactTypeColumnIndex),
                            cursor.getString(contactEmailColumnIndex)));
                }
            }
        }
        return tempList;
    }
}
