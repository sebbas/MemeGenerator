package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.fragments.ExploreFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

public class ExploreFragmentAdapter extends SlidingTabsAdapter {

    private static final String TAG = "ExploreFragmentAdapter";

    public ExploreFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        boolean isRefreshable, isNetworkEnabled;

        switch (position) {
            case 0:
                isRefreshable = true;
                isNetworkEnabled = true;
                fragment = RecyclerFragment.newInstance(
                        ExploreFragment.TAG_CHILD_ONE,
                        RecyclerFragment.LIST_LAYOUT,
                        RecyclerFragment.EXPLORE_DETAIL,
                        isRefreshable, position, isNetworkEnabled);
                break;
            case 1:
                isRefreshable = true;
                isNetworkEnabled = true;
                fragment = RecyclerFragment.newInstance(
                        ExploreFragment.TAG_CHILD_TWO,
                        RecyclerFragment.LIST_LAYOUT,
                        RecyclerFragment.EXPLORE,
                        isRefreshable, position, isNetworkEnabled);
                break;
            default:
                isRefreshable = true;
                isNetworkEnabled = true;
                fragment = RecyclerFragment.newInstance(
                        ExploreFragment.TAG_CHILD_ONE,
                        RecyclerFragment.LIST_LAYOUT,
                        RecyclerFragment.EXPLORE,
                        isRefreshable, position, isNetworkEnabled);
        }
        return fragment;
    }

}