package com.epam.rd.advphone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.adapters.SmsRecyclerViewAdapter;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.viewmodels.SmsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class SmsFragment extends Fragment implements ContactCommunicator {
    private View view;
    private TextView userHasNotSmsTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sms_fragment, container, false);
        userHasNotSmsTv = view.findViewById(R.id.userHasNotSmsTv);

        FloatingActionButton newSmsFab = view.findViewById(R.id.newSmsFab);
        newSmsFab.setOnClickListener(v -> showSmsActivity(view, null, null));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        RecyclerView allSmsRecycler = view.findViewById(R.id.allSmsRecycler);
        allSmsRecycler.setHasFixedSize(true);
        allSmsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        SmsRecyclerViewAdapter adapter = new SmsRecyclerViewAdapter();
        allSmsRecycler.setAdapter(adapter);

        ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SmsViewModel.class).getSmsListLive().observe(this, sms -> {
            userHasNotSmsTv.setVisibility(sms.isEmpty() ? View.VISIBLE : View.GONE);
            adapter.setSmsList(sms);
        });
    }
}
