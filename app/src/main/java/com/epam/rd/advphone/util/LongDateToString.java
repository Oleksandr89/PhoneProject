package com.epam.rd.advphone.util;

import android.content.Context;
import android.text.format.DateFormat;

import com.epam.rd.advphone.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LongDateToString {
    private static final int MILLISECONDS_IN_ONE_DAY = 86400000;
    private static final int MILLISECONDS_IN_ONE_HOUR = 3600000;
    private static final int MILLISECONDS_IN_ONE_MINUTE = 60000;
    private static final int MILLISECONDS_IN_ONE_SECOND = 1000;
    private static final int MINUTES_IN_ONE_HOUR = 60;
    private static final SimpleDateFormat simpleDateFormat;
    private static boolean is24HourFormat;
    private static final String DAY_FORMAT_12;
    private static final String DAY_FORMAT_24;
    private static final String MORE_THAN_DAY_FORMAT_12;
    private static final String MORE_THAN_DAY_FORMAT_24;

    static {
        DAY_FORMAT_12 = "HH:mm a";
        DAY_FORMAT_24 = "HH:mm";
        MORE_THAN_DAY_FORMAT_12 = "dd.MM\nHH:mm a";
        MORE_THAN_DAY_FORMAT_24 = "dd.MM\nHH:mm";
        //default simpleDateFormat
        simpleDateFormat = new SimpleDateFormat(DAY_FORMAT_12, Locale.ENGLISH);
    }

    private LongDateToString() {
    }

    public static String convert(Context context, Long value) {
        if (context != null && value != null) {
            is24HourFormat = DateFormat.is24HourFormat(context);
            long currentTime = System.currentTimeMillis();
            long msDiff = currentTime - value;

            if (msDiff <= MILLISECONDS_IN_ONE_MINUTE) {
                return context.getString(R.string.seconds, msDiff / MILLISECONDS_IN_ONE_SECOND);
            } else if (msDiff <= MILLISECONDS_IN_ONE_HOUR) {
                return context.getString(R.string.minutes, (msDiff / MILLISECONDS_IN_ONE_SECOND) / MINUTES_IN_ONE_HOUR);
            } else {
                simpleDateFormat.applyPattern(msDiff <= MILLISECONDS_IN_ONE_DAY ? getDayFormat() : getMoreThanOneDayFormat());
                return simpleDateFormat.format(value);
            }
        } else {
            return "";
        }
    }

    private static String getDayFormat() {
        return is24HourFormat ? DAY_FORMAT_24 : DAY_FORMAT_12;
    }

    private static String getMoreThanOneDayFormat() {
        return is24HourFormat ? MORE_THAN_DAY_FORMAT_24 : MORE_THAN_DAY_FORMAT_12;
    }
}
