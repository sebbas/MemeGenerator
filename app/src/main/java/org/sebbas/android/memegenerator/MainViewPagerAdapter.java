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

import java.util.ArrayList;
import java.util.List;


public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "MainViewPagerAdapter";
    private int[] mIcons;
    private int mNumbOfTabs;
    private ActionBarActivity mActivity;

    private SimpleFragment mTemplateFragment;
    private SlidingTabsFragment mInstanceFragment;
    private SlidingTabsFragment mGalleryFragment;
    private SimplePreferenceFragment mPreferencesFragment;

    public MainViewPagerAdapter(Activity activity, FragmentManager fm, int[] icons, int numbOfTabs) {
        super(fm);

        mActivity = (ActionBarActivity) activity;
        mIcons = icons;
        mNumbOfTabs = numbOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        String[] tabTitlesInstances = MemeGeneratorApplication.getAppContext()
                .getResources().getStringArray(R.array.tab_titles_instances);

        String[] tabTitlesGallery = MemeGeneratorApplication.getAppContext()
                .getResources().getStringArray(R.array.tab_titles_gallery);

        switch (position) {
            case 0:
                if (mTemplateFragment == null) {
                    mTemplateFragment = SimpleFragment.newInstance(
                            R.string.templates);
                }
                return mTemplateFragment;
            case 1:
                if (mInstanceFragment == null) {
                    mInstanceFragment = SlidingTabsFragment.newInstance(
                            R.string.instances, tabTitlesInstances);
                }
                return mInstanceFragment;
            case 2:
                if (mGalleryFragment == null) {
                    mGalleryFragment = SlidingTabsFragment.newInstance(
                            R.string.gallery, tabTitlesGallery);
                }
                return mGalleryFragment;
            case 3:
                if (mPreferencesFragment == null) {
                    mPreferencesFragment = SimplePreferenceFragment.newInstance(
                            R.string.preferences);
                }
                return mPreferencesFragment;
            default:
                return PreferencesFragment.newInstance();
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
