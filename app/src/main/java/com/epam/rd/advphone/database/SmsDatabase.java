package com.epam.rd.advphone.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.epam.rd.advphone.models.Sms;

@Database(entities = {Sms.class}, version = 1, exportSchema = false)
public abstract class SmsDatabase extends RoomDatabase {
    private static SmsDatabase INSTANCE;
    public abstract SmsDao smsDao();

    public static SmsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SmsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, SmsDatabase.class, DatabaseStringsConstants.SMS_DB_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
