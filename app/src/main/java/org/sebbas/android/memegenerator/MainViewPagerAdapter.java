package org.sebbas.android.memegenerator;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Filter;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainViewPagerAdapter extends CacheFragmentStatePagerAdapter {

    private static final String TAG = "MainViewPagerAdapter";
    private static final String[] TITLES_INSTANCES = {"Viral", "New", "Top Today", "Top Week",
            "Top Month", "Top Year", "Top All"};
    private static final String[] TITLES_GALLERY = {"My Memes", "Recent", "Favorites"};

    private int[] mIcons;
    private int mNumbOfTabs;

    public MainViewPagerAdapter(FragmentManager fm, int[] icons, int numbOfTabs) {
        super(fm);
        mIcons = icons;
        mNumbOfTabs = numbOfTabs;
    }

    /*@Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SimpleFragment.newInstance(R.string.templates);
            case 1:
                return SlidingTabsFragment.newInstance(R.string.instances, TITLES_INSTANCES);
            case 2:
                return SlidingTabsFragment.newInstance(R.string.gallery, TITLES_GALLERY);
            case 3:
                return SimplePreferenceFragment.newInstance(R.string.preferences);
            default:
                return SlidingTabsFragment.newInstance(R.string.instances, TITLES_INSTANCES);
        }
    }*/

    @Override
    protected Fragment createItem(int position) {
        switch (position) {
            case 0:
                return SimpleFragment.newInstance(R.string.templates);
            case 1:
                return SlidingTabsFragment.newInstance(R.string.instances, TITLES_INSTANCES);
            case 2:
                return SlidingTabsFragment.newInstance(R.string.gallery, TITLES_GALLERY);
            case 3:
                return SimplePreferenceFragment.newInstance(R.string.preferences);
            default:
                return SlidingTabsFragment.newInstance(R.string.instances, TITLES_INSTANCES);
        }
    }

    @Override
    public int getCount() {
        return mNumbOfTabs;
    }

    public int getDrawableId(int position) {
        return mIcons[position];
    }

}
