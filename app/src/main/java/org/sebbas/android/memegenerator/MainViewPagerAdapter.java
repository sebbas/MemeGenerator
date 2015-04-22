package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] TITLES_INSTANCES = new String[] {"Viral", "Time", "Top Today", "Top Week", "Top Month", "Top Year", "Top All"};

    private int[] mIcons;
    private int mNumbOfTabs;

    private MemeFragment mTemplateFragment;
    private MemeFragment mInstanceFragment;
    private PreferencesFragment mGalleryFragment;
    private PreferencesFragment mPreferencesFragment;

    public MainViewPagerAdapter(FragmentManager fm, int[] icons, int numbOfTabs) {
        super(fm);

        mIcons = icons;
        mNumbOfTabs = numbOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (mTemplateFragment == null) {
                    mTemplateFragment = MemeFragment.newInstance(TITLES_INSTANCES);
                }
                return mTemplateFragment;
            case 1:
                if (mInstanceFragment == null) {
                    mInstanceFragment = MemeFragment.newInstance(TITLES_INSTANCES);
                }
                return mInstanceFragment;
            case 2:
                if (mGalleryFragment == null) {
                    mGalleryFragment = PreferencesFragment.newInstance();
                }
                return mGalleryFragment;
            case 3:
                if (mPreferencesFragment == null) {
                    mPreferencesFragment = PreferencesFragment.newInstance();
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
