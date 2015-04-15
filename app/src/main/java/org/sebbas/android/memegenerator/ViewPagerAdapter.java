package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence mTitles[];
    private int[] mIcons;
    private int mNumbOfTabs;

    private Fragment mMemeFragment;
    private EditorFragment mEditorFragment;
    private EditorFragment mGalleryFragment;
    private PreferencesFragment mPreferencesFragment;

    private FragmentManager mFragmentManager;

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int[] icons, int numbOfTabs) {
        super(fm);

        mTitles = titles;
        mIcons = icons;
        mNumbOfTabs = numbOfTabs;
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (mMemeFragment == null) {
                    mMemeFragment = MemeFragment.newInstance();
                }
                return mMemeFragment;
            case 1:
                if (mEditorFragment == null) {
                    mEditorFragment = EditorFragment.newInstance();
                }
                return mEditorFragment;
            case 2:
                if (mGalleryFragment == null) {
                    mGalleryFragment = EditorFragment.newInstance();
                }
                return mGalleryFragment;
            case 3:
                if (mPreferencesFragment == null) {
                    mPreferencesFragment = PreferencesFragment.newInstance();
                }
                return mPreferencesFragment;
            default:
                return EditorFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mNumbOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public int getDrawableId(int position) {
        return mIcons[position];
    }
}
