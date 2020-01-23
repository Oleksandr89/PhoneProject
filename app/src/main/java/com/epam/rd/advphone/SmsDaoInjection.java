package com.epam.rd.advphone;

import android.content.Context;

import androidx.annotation.NonNull;

import com.epam.rd.advphone.database.SmsDao;
import com.epam.rd.advphone.database.SmsDatabase;

public class SmsDaoInjection {
    public static SmsDao provideSmsDao (@NonNull Context context) {
        return SmsDatabase.getInstance(context).smsDao();
    }
}
