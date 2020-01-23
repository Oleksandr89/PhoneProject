package com.epam.rd.advphone.util;

import com.epam.rd.advphone.models.Contact;

public interface OnContactEditClickListener {
    void onFavouriteClick(int position, Contact contact);
    void onEditClick(Contact contact);
}
