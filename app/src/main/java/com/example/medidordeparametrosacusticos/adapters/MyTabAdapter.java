package com.example.medidordeparametrosacusticos.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.medidordeparametrosacusticos.fragments.FileViewerFragment;
import com.example.medidordeparametrosacusticos.fragments.RecordFragment;

public class MyTabAdapter extends FragmentStateAdapter {

    public MyTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RecordFragment();
            case 1:
                return new FileViewerFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
