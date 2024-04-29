package com.example.assgn4;

import androidx.annotation.NonNull;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.fragment.app.Fragment;


public class ViewPagerAdapter extends FragmentStateAdapter {
    // Constructor and other methods...

    public ViewPagerAdapter(@NonNull DetailFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FirstTabFragment();
            case 1:
                return new SecondTabFragment();
            default:
                throw new IllegalStateException("Unexpected position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}

