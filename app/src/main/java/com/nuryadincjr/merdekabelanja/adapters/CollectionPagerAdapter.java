package com.nuryadincjr.merdekabelanja.adapters;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.nuryadincjr.merdekabelanja.usersfragment.ItemViewPagerFragment;

public class CollectionPagerAdapter extends FragmentStateAdapter {

    private final int tabCount;
    private final String category;
    private final TabLayout tablayout;

    public CollectionPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                  int tabCount, String category, TabLayout tablayout) {
        super(fragmentActivity);
        this.tabCount = tabCount;
        this.category = category;
        this.tablayout = tablayout;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(tabCount==1) tablayout.setVisibility(View.GONE);
        else if(tabCount==10) tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        Fragment fragment = new ItemViewPagerFragment();
        Bundle args = new Bundle();

        args.putString(ItemViewPagerFragment.ARG_CATEGORY, category);
        args.putInt(ItemViewPagerFragment.ARG_TAB_INDEX, position);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return tabCount;
    }

}
