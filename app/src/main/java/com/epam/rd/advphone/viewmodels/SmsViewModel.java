package com.epam.rd.advphone.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.epam.rd.advphone.SmsDaoInjection;
import com.epam.rd.advphone.database.SmsDao;
import com.epam.rd.advphone.models.Sms;

import java.util.List;
import java.util.concurrent.Executors;

public class SmsViewModel extends AndroidViewModel {
    private final SmsDao smsDao;
    private final LiveData<List<Sms>> smsListLive;
    private final MutableLiveData<String> recipientNumberLive;

    public SmsViewModel(@NonNull Application application) {
        super(application);
        smsDao = SmsDaoInjection.provideSmsDao(application);
        smsListLive = smsDao.getSms();
        recipientNumberLive = new MutableLiveData<>();
    }

    public LiveData<List<Sms>> getSmsListLive() {
        return smsListLive;
    }

    public void setRecipientNumber(String recipientNumber) {
        recipientNumberLive.setValue(recipientNumber);
    }

    public LiveData<List<Sms>> getSmsByRecipient() {
        return Transformations.switchMap(recipientNumberLive, smsDao::getSmsByRecipient);
    }

    public void deleteMessagesByRecipient(String recipientNumber) {
        Executors.newSingleThreadExecutor().execute(() -> smsDao.deleteMessagesByRecipient(recipientNumber));
    }

    public void deleteMessagesById(int recipientId) {
        Executors.newSingleThreadExecutor().execute(() -> smsDao.deleteMessagesById(recipientId));
    }
}
