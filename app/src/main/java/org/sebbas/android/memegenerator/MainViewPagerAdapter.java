package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int INSTANCES_FRAGMENT_POSITION = 1;
    public static final int GALLERY_FRAGMENT_POSITION = 3;

    private static final String TAG = "MainViewPagerAdapter";

    private static int[] TITLES_INSTANCES = {
            R.string.all_images,
            R.string.memes,
            R.string.gifs};

    private static final int[] TITLES_GALLERY = {
            R.string.my_memes,
            R.string.favorites,
            R.string.recent};

    private static final int mIcons[] = {
            R.drawable.selector_template_icon,
            R.drawable.selector_instances_icon,
            R.drawable.selector_explore_icon,
            R.drawable.selector_gallery_icon,
            R.drawable.selector_preferences_icon};

    private int mNumbOfTabs = 5;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SimpleFragment.newInstance();
            case 1:
                return SlidingTabsFragment.newInstance(INSTANCES_FRAGMENT_POSITION, TITLES_INSTANCES);
            case 2:
                return ExploreFragment.newInstance();
            case 3:
                return SlidingTabsFragment.newInstance(GALLERY_FRAGMENT_POSITION, TITLES_GALLERY);
            case 4:
                return SimplePreferenceFragment.newInstance();
            default:
                return SimpleFragment.newInstance();
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
