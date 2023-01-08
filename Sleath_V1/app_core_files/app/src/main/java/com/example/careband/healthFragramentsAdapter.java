package com.example.careband;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class healthFragramentsAdapter extends FragmentStateAdapter {
    private String[] titles = new String[]{"Status","heartBeat"};

    public healthFragramentsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new statusFragment();
            case 1:
                return new heartBeatFragment();
        }
        return new statusFragment();
    }
    @Override
    public int getItemCount() {
        return titles.length;
    }
}
