package com.epam.rd.advphone.repositories;

import com.epam.rd.advphone.models.Contact;

import java.util.List;

public interface ContactsProvider {
    List<Contact> getContacts();
}
