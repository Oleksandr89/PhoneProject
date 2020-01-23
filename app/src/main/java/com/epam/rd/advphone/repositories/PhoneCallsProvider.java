package com.epam.rd.advphone.repositories;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.epam.rd.advphone.models.Call;
import com.epam.rd.advphone.viewmodels.CallsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhoneCallsProvider implements CallsProvider {
    private static PhoneCallsProvider phoneCallsProvider;
    private final ContentResolver resolver;
    private final CallsViewModel callsViewModel;

    private PhoneCallsProvider(Context context) {
        this.resolver = context.getContentResolver();
        this.callsViewModel = ViewModelProviders.of((FragmentActivity) context).get(CallsViewModel.class);
    }

    public static PhoneCallsProvider getInstance(Context context) {
        if (phoneCallsProvider == null) {
            phoneCallsProvider = new PhoneCallsProvider(context);
        }
        return phoneCallsProvider;
    }

    @Override
    public List<Call> getCalls() {
        List<Call> callList = new ArrayList<>();
        Uri uri = CallLog.Calls.CONTENT_URI;
        try (@SuppressLint("MissingPermission") Cursor cursor = resolver.query(uri, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER)) {

            while (Objects.requireNonNull(cursor).moveToNext()) {

                String callPhoto = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    callPhoto = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI));
                }

                Call call = new Call.Builder()
                        .setId(cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID)))
                        .setName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)))
                        .setPhone(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)))
                        .setType(checkCallType(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))))
                        .setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)))
                        .setPhoto(callPhoto)
                        .setDuration(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)))
                        .build();

                callList.add(call);
            }
        }
        return callList;
    }

    private String checkCallType(String callType) {
        String dirCode = null;
        int typeCode = Integer.parseInt(callType);

        switch (typeCode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dirCode = "OUTGOING";
                break;
            case CallLog.Calls.INCOMING_TYPE:
                dirCode = "INCOMING";
                break;
            case CallLog.Calls.MISSED_TYPE:
                dirCode = "MISSED";
                break;
        }
        return dirCode;
    }

    public Call lookupContactInfo(Call call) {
        Cursor cursor = null;
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(call.getPhone()));
            cursor = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI}, null, null, null);
        } catch (IllegalArgumentException e) {
            //This is sometimes thrown when number is in invalid format, so phone cannot recognize it.
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToNext()) {
                call.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
                call.setPhoto(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI)));
            }
            cursor.close();
        }
        return call;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void deleteCalls(int id) {
        resolver.delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + "=?", new String[]{String.valueOf(id)});
        callsViewModel.refreshCallsLog();
    }
}
