package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;

import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.SlidingTabsFragment;


public abstract class SlidingTabsAdapter extends CacheFragmentStatePagerAdapter {

    private static final String TAG = "SlidingTabsFragmentAdapter";

    private Context mContext;
    private int[] mTitleResources;
    private int mScrollY;

    public SlidingTabsAdapter(Context context, FragmentManager fragmentManager,
                              int[] titleResources) {
        super(fragmentManager);

        mContext = context;
        mTitleResources = titleResources;
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
        return Utils.resourceArrayToStringArray(mContext, mTitleResources);
    }

    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
    }

    public int getScrollY() {
        return mScrollY;
    }
}