package com.epam.rd.advphone.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.epam.rd.advphone.models.Sms;

import java.util.List;

import static com.epam.rd.advphone.database.DatabaseStringsConstants.*;

@Dao
public interface SmsDao {

    @Query("SELECT * FROM " + SMS_DB_NAME + " WHERE " + SMS_RECIPIENT_NUMBER + " = :recipientNumber ORDER BY " + SMS_TIME_STAMP)
    LiveData<List<Sms>> getSmsByRecipient(String recipientNumber);

    @Query("SELECT * FROM (SELECT * FROM " + SMS_DB_NAME + " ORDER BY " + SMS_ID + " ASC ) " +
            "group by "+ SMS_RECIPIENT_NUMBER + " ORDER BY " + SMS_ID + " DESC")
    LiveData<List<Sms>> getSms();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSms(Sms sms);

    @Query("DELETE FROM " + SMS_DB_NAME + " WHERE " + SMS_RECIPIENT_NUMBER + " = :recipientNumber")
    void deleteMessagesByRecipient(String recipientNumber);

    @Query("DELETE FROM " + SMS_DB_NAME + " WHERE " + SMS_ID + " = :messageId")
    void deleteMessagesById(int messageId);

//    @Query("SELECT COUNT(" + SMS_ID + ") FROM " + SMS_DB_NAME + " WHERE " + SMS_RECIPIENT_NUMBER + " = :recipientNumber")
//    int existInDatabase(String recipientNumber);
}
