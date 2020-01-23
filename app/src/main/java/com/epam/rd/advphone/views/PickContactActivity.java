package com.epam.rd.advphone.views;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.adapters.PickContactRecyclerViewAdapter;
import com.epam.rd.advphone.viewmodels.PickContactViewModel;

public class PickContactActivity extends AppCompatActivity {
    private PickContactViewModel pickContactViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);

        //init pickContactViewModel
        pickContactViewModel = ViewModelProviders.of(this).get(PickContactViewModel.class);
        pickContactViewModel.getContactsLive().observe(this, contacts -> {
            pickContactViewModel.setContactsList(contacts);
            String pattern = pickContactViewModel.getPattern().getValue();
            pickContactViewModel.setPattern(pattern == null ? "" : pattern);
        });

        setupPickContactRecycler();

        setupSearchView();
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    pickContactViewModel.setPattern(newText);
                } else {
                    pickContactViewModel.setPattern("");
                }
                return true;
            }
        });
    }

    private void setupPickContactRecycler() {
        RecyclerView pickContactRecycler = findViewById(R.id.pickContactRecycler);
        pickContactRecycler.setLayoutManager(new LinearLayoutManager(this));
        PickContactRecyclerViewAdapter adapter = new PickContactRecyclerViewAdapter();
        pickContactRecycler.setAdapter(adapter);
        pickContactViewModel.getPattern().observe(this, s -> adapter.setContacts(pickContactViewModel.getContacts()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
