package com.example.laporan2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminPagerAdapter extends FragmentStateAdapter {
    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new NewRequestsFragment() : new EditRequestsFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Dua halaman: New Requests dan Edit Requests
    }
}