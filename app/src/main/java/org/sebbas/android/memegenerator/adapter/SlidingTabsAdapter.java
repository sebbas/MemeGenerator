package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;

import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.fragments.SlidingTabsFragment;

public abstract class SlidingTabsAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "SlidingTabsFragmentAdapter";

    private String[] mTitles;
    private SparseArray<BaseFragment> mRegisteredFragments;
    protected FragmentManager mFragmentManager;

    private SlidingTabsAdapter(FragmentManager fragmentManager, int titleSize) {
        super(fragmentManager);
        mTitles = new String[titleSize];

        if (mRegisteredFragments == null) {
            mRegisteredFragments = new SparseArray<>();
        }
        mFragmentManager = fragmentManager;
    }

    public SlidingTabsAdapter(Context context, FragmentManager fragmentManager,
                              int[] titleResources) {
        this(fragmentManager, titleResources.length);
        mTitles = Utils.resourceArrayToStringArray(context, titleResources);
    }

    public SlidingTabsAdapter(FragmentManager fragmentManager, String[] titles) {
        this(fragmentManager, titles.length);
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return getTitles().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getTitles()[position];
    }

    private String[] getTitles() {
        return mTitles;
    }

    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseFragment baseFragment = (BaseFragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, baseFragment);
        return baseFragment;
    }
}