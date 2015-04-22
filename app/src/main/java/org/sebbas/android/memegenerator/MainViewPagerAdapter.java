package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private int[] mIcons;
    private int mNumbOfTabs;

    private SimpleFragment mTemplateFragment;
    private SlidingTabsFragment mInstanceFragment;
    private SlidingTabsFragment mGalleryFragment;
    private SimplePreferenceFragment mPreferencesFragment;

    public MainViewPagerAdapter(FragmentManager fm, int[] icons, int numbOfTabs) {
        super(fm);

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
