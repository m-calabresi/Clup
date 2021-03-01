package com.android.clup.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android.clup.ui.SelectFragment;
import com.android.clup.viewmodel.SelectViewModel;

/**
 * A {@link FragmentStateAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {
    private final int itemCount;

    public SectionsPagerAdapter(@NonNull final FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        final SelectViewModel viewModel = new ViewModelProvider(fragmentActivity).get(SelectViewModel.class);
        this.itemCount = viewModel.getSelectedShop().getAvailableDays().size();
    }

    @NonNull
    @Override
    public Fragment createFragment(final int position) {
        final Bundle args = new Bundle();
        args.putInt(SelectFragment.KEY_POSITION, position);

        final Fragment fragment = new SelectFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return this.itemCount;
    }
}
