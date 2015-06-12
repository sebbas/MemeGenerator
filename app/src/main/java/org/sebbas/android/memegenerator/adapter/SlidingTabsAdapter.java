package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

    private Context mContext;
    private int[] mTitleResources;
    private SparseArray<Fragment> mRegisteredFragments;


    public SlidingTabsAdapter(Context context, FragmentManager fragmentManager,
                              int[] titleResources) {
        super(fragmentManager);

        mContext = context;
        mTitleResources = titleResources;

        if (mRegisteredFragments == null) {
            mRegisteredFragments = new SparseArray<>();
        }
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

    public Fragment getRegisteredFragment(int position) {
        Log.d(TAG, "size is " + mRegisteredFragments.size());
        return mRegisteredFragments.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }
}