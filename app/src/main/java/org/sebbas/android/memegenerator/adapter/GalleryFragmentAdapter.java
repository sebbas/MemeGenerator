package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.fragments.GalleryFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

public class GalleryFragmentAdapter extends SlidingTabsAdapter {

    private static final String TAG = "GalleryFragmentAdapter";

    public GalleryFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        boolean isRefreshable, isNetworkEnabled;


        switch (position) {
            case 0:
                isRefreshable = false;
                isNetworkEnabled = true;
                fragment = RecyclerFragment.newInstance(
                        GalleryFragment.TAG_CHILD_ONE,
                        RecyclerFragment.GRID_LAYOUT,
                        RecyclerFragment.CARD,
                        isRefreshable, position, isNetworkEnabled);
                break;
            case 1:
                isRefreshable = false;
                isNetworkEnabled = true;
                fragment = RecyclerFragment.newInstance(
                        GalleryFragment.TAG_CHILD_TWO,
                        RecyclerFragment.GRID_LAYOUT,
                        RecyclerFragment.CARD,
                        isRefreshable, position, isNetworkEnabled);
                break;
            case 2:
                isRefreshable = false;
                isNetworkEnabled = true;
                fragment = RecyclerFragment.newInstance(
                        GalleryFragment.TAG_CHILD_THREE,
                        RecyclerFragment.GRID_LAYOUT,
                        RecyclerFragment.CARD,
                        isRefreshable, position, isNetworkEnabled);
                break;
            default:
                isRefreshable = false;
                isNetworkEnabled = true;
                fragment = RecyclerFragment.newInstance(
                        GalleryFragment.TAG_CHILD_ONE,
                        RecyclerFragment.GRID_LAYOUT,
                        RecyclerFragment.CARD,
                        isRefreshable, position, isNetworkEnabled);
        }
        return fragment;
    }

}
