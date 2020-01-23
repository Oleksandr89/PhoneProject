package com.epam.rd.advphone.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.ContactDaoInjection;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.adapters.ContactRecyclerViewAdapter;
import com.epam.rd.advphone.adapters.MainViewPager2Adapter;
import com.epam.rd.advphone.databinding.ActivityMainBinding;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.repositories.CallsProvider;
import com.epam.rd.advphone.repositories.ContactsProvider;
import com.epam.rd.advphone.repositories.PhoneCallsProvider;
import com.epam.rd.advphone.repositories.PhoneContactsProvider;
import com.epam.rd.advphone.viewmodels.CallsViewModel;
import com.epam.rd.advphone.viewmodels.ContactsViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;
import java.util.concurrent.Executors;

import static com.epam.rd.advphone.Constants.CONTACT;
import static com.epam.rd.advphone.RequestCodes.REQUEST_EDIT_CONTACT;
import static com.epam.rd.advphone.RequestCodes.REQUEST_NEW_CONTACT;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private int[] tabTitles;
    private String tabPanelLocation;
    private TabLayout tabLayout;
    private RecyclerView searchContactRecycler;
    private ContactsViewModel contactsViewModel;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mainBinding.toolBar);

        //getting contacts from address book
        //and insertion to the database
        retrieveContacts();

        //getting info about tabs location from shared preferences
        getTabLocation();

        initTabTitles();
        initTabLayoutAndViewPager();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //init ContactsViewModel for contacts searching
        contactsViewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);

        //find contacts by name
        findContactsFeature();

        initSearchContactRecycler();
    }

    private void findContactsFeature() {
        mainBinding.searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                searchContactRecycler.setVisibility(View.VISIBLE);
                mainBinding.LlForBottomPanel.setVisibility(View.GONE);
            }
        });

        mainBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    contactsViewModel.setContactsFilteringText("%" + newText + "%");
                } else {
                    contactsViewModel.setContactsFilteringText("");
                }
                return true;
            }
        });
    }

    private void initSearchContactRecycler() {
        searchContactRecycler = mainBinding.searchContactRecycler;
        searchContactRecycler.setHasFixedSize(true);
        searchContactRecycler.setLayoutManager(new LinearLayoutManager(this));
        ContactRecyclerViewAdapter adapter = new ContactRecyclerViewAdapter(contactsViewModel);
        searchContactRecycler.setAdapter(adapter);
        contactsViewModel.getFoundContacts().observe(this, adapter::setContacts);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == RequestCodes.PERMISSION_READ_CONTACTS) {
                retrieveContacts();
            } else if (requestCode == RequestCodes.PERMISSION_READ_CALL_LOG) {
                readCallLog();
            }
        }
    }

    private void retrieveContacts() {
        String permission = Manifest.permission.READ_CONTACTS;
        if (PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, RequestCodes.PERMISSION_READ_CONTACTS);
        } else {
            ContactsProvider contactsProvider = PhoneContactsProvider.getInstance(this);
            Executors.newSingleThreadExecutor().submit(() -> ContactDaoInjection.provideContactsDao(this)
                    .insertContacts(contactsProvider.getContacts()));
        }
    }

    private void readCallLog() {
        CallsProvider callsProvider = PhoneCallsProvider.getInstance(this);
        CallsViewModel callsViewModel = ViewModelProviders.of(this).get(CallsViewModel.class);
        callsViewModel.setCallsProvider(callsProvider);
        callsViewModel.getCallsLogList().setValue(callsProvider.getCalls());
    }

    private void getTabLocation() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        tabPanelLocation = preferences.getString(Constants.PANEL_LOCATION, getString(R.string.tag_location_top));
    }

    private void initTabLayoutAndViewPager() {
        MainViewPager2Adapter viewPager2Adapter = new MainViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        mainBinding.viewPager2.setAdapter(viewPager2Adapter);

        createTabLayout();

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout,
                mainBinding.viewPager2, (tab, position) -> tab.setText(tabTitles[position]));
        tabLayoutMediator.attach();
    }

    private void createTabLayout() {
        tabLayout = new TabLayout(this);
        tabLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_AUTO);
        tabLayout.setTabTextColors(Color.DKGRAY, getResources().getColor(R.color.colorAccent));

        //setup tabLayout location
        if (tabPanelLocation.equals(getString(R.string.tag_location_bottom))) {
            mainBinding.LlForBottomPanel.addView(tabLayout);
        } else {
            mainBinding.LlForTopPanel.addView(tabLayout);
        }
    }

    private void initTabTitles() {
        tabTitles = new int[]{R.string.keypad, R.string.recent, R.string.messages, R.string.contacts};
    }

    //init main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsItem) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showContactActivity(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivityForResult(intent, REQUEST_NEW_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Contact contact;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_NEW_CONTACT) {
                contact = Objects.requireNonNull(data).getParcelableExtra(CONTACT);
                if (contact != null) {
                    validateContactPhone(contact);
                    ViewModelProviders.of(this).get(ContactsViewModel.class).insertContact(contact);
                }
            } else if (requestCode == REQUEST_EDIT_CONTACT) {
                contact = Objects.requireNonNull(data).getParcelableExtra(CONTACT);
                if (contact != null) {
                    validateContactPhone(contact);
                    ViewModelProviders.of(this).get(ContactsViewModel.class).updateContact(contact);
                }
            }
        }
    }

    private void validateContactPhone(Contact contact) {
        if (contact != null) {
            contact.setPhone(contact.getPhone()
                    .trim()
                    .replace(" ", ""));
        }
    }

    @Override
    public void onBackPressed() {
        if (mainBinding.searchContactRecycler.getVisibility() == View.VISIBLE) {
            mainBinding.searchView.setQuery("", false);
            mainBinding.searchContactRecycler.setVisibility(View.GONE);
            mainBinding.LlForBottomPanel.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}