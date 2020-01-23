package com.epam.rd.advphone.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.epam.rd.advphone.views.ContactsFragment;
import com.epam.rd.advphone.views.KeypadFragment;
import com.epam.rd.advphone.views.RecentFragment;
import com.epam.rd.advphone.views.SmsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewPager2Adapter extends FragmentStateAdapter {
    private final List<Fragment> fragments;

    public MainViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

        //init list of fragments
        fragments = new ArrayList<>();
        fragments.add(new KeypadFragment());
        fragments.add(new RecentFragment());
        fragments.add(new SmsFragment());
        fragments.add(new ContactsFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
