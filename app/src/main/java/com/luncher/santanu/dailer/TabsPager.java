package com.luncher.santanu.dailer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.luncher.santanu.dailer.fragment.ContactFragment;
import com.luncher.santanu.dailer.fragment.FrequentFragment;
import com.luncher.santanu.dailer.fragment.RecentFragment;

/**
 * Created by santanu on 26/3/18.
 */

public class TabsPager extends FragmentStatePagerAdapter {

    String[] titles = new String[]{"Frequent","Recent","Contacts"};

    public TabsPager(FragmentManager fm) {
        super(fm);
    }

    public CharSequence getPageTitle(int Position){
        return titles[Position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                FrequentFragment frequentFragment = new FrequentFragment();
                return frequentFragment;
            case 1:
                RecentFragment recentFragment = new RecentFragment();
                return recentFragment;
            case 2:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
