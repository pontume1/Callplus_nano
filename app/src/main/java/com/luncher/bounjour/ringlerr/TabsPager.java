package com.luncher.bounjour.ringlerr;

import com.luncher.bounjour.ringlerr.fragment.ContactFragment;
import com.luncher.bounjour.ringlerr.fragment.FrequentFragment;
import com.luncher.bounjour.ringlerr.fragment.RecentFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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
