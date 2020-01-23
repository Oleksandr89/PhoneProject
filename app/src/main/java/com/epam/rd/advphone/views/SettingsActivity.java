package com.epam.rd.advphone.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.databinding.ActivitySettingsBinding;

import static com.epam.rd.advphone.Constants.PANEL_LOCATION;
import static com.epam.rd.advphone.Constants.SHARED_PREFERENCES_NAME;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding settingsBinding;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        initActiveRadioButton();
    }

    private void initActiveRadioButton() {
        String tag = preferences.getString(PANEL_LOCATION, getString(R.string.tag_location_top));
        ((RadioButton)settingsBinding.tabPanelLocationRadioGroup.findViewWithTag(tag)).setChecked(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveToSharedPref();
    }

    //saving selected radioButton into shared preferences
    private void saveToSharedPref() {
        int id = settingsBinding.tabPanelLocationRadioGroup.getCheckedRadioButtonId();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PANEL_LOCATION, settingsBinding.tabPanelLocationRadioGroup.findViewById(id).getTag().toString());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
