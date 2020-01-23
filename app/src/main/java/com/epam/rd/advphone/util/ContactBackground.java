package com.epam.rd.advphone.util;

import android.content.Context;
import android.widget.TextView;

import com.epam.rd.advphone.R;

public class ContactBackground {
    private static int i;
    private static char previousChar;
    private static int[] arrayOfColors;


    public static int getColor(Context context, char presentChar) {
        if (arrayOfColors == null) {
            arrayOfColors = context.getResources().getIntArray(R.array.colorPaletteForContacts);
        }

        if (previousChar != presentChar) {
            previousChar = presentChar;
            if (i < arrayOfColors.length - 1) i++;
            else i=0;
        }
        return arrayOfColors[i];
    }

    public static void setContactIcon(TextView textView, String name){
        if (name != null) {
            textView.setBackgroundResource(R.drawable.background_of_existing_contact);
            textView.setText(String.valueOf(name.charAt(0)));
        } else {
            textView.setBackgroundResource(R.drawable.background_of_not_existing_contact);
            textView.setText("");
        }
    }
}
