package com.epam.rd.advphone.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.epam.rd.advphone.models.Call;
import com.epam.rd.advphone.repositories.CallsProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallsViewModel extends ViewModel {

    private CallsProvider callsProvider;
    private MutableLiveData<List<Call>> callsLogList;

    public MutableLiveData<List<Call>> getCallsLogList() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        if (callsLogList == null) {
            callsLogList = new MutableLiveData<>();
            executorService.execute(() -> {
                if (callsProvider != null) {
                    callsLogList.postValue(callsProvider.getCalls());
                }
            });
        }
        return callsLogList;
    }

    public void setCallsProvider(CallsProvider callsProvider) {
        this.callsProvider = callsProvider;
    }

    public void refreshCallsLog() {
        callsLogList.setValue(callsProvider.getCalls());
    }
}
