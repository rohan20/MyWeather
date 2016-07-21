package com.coppermobile.myweather.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.coppermobile.myweather.TabLayoutFragments.CurrentFragment;
import com.coppermobile.myweather.TabLayoutFragments.FiveDaysFragment;


public class ViewPagerFragmentsAdapter extends FragmentPagerAdapter {

    Context mContext;

    public ViewPagerFragmentsAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0)
            return new CurrentFragment();
        else
            return new FiveDaysFragment();

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0)
            return "Current";

        return "5 days";
    }
}
