package com.epam.rd.advphone.database;

public interface DatabaseStringsConstants {
    String DB_NAME = "contacts";
    String CONTACT_ID = "contactId";
    String CONTACT_NAME = "contactName";
    String CONTACT_PHONE = "contactPhone";
    String CONTACT_IMAGE = "contactImage";
    String CONTACT_FAVOURITE = "isFavourite";
    String CONTACT_TYPE = "contactType";
    String CONTACT_EMAIL = "contactEmail";

    //constants for sms database
    String SMS_DB_NAME = "sms_db";
    String SMS_ID = "smsId";
    String SMS_RECIPIENT_NUMBER = "recipientNumber";
    String SMS_RECIPIENT_NAME = "recipientName";
    String SMS_TEXT = "messageText";
    String SMS_IS_RECIPIENT = "isRecipient";
    String SMS_TIME_STAMP = "timeStamp";
}
