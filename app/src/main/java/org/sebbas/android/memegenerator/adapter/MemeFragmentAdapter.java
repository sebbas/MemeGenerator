package org.sebbas.android.memegenerator.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.fragments.GifChildFragment;
import org.sebbas.android.memegenerator.fragments.MemeChildFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

public class MemeFragmentAdapter extends SlidingTabsAdapter {

    private static final String TAG = "GifFragmentAdapter";

    public MemeFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    @Override
    protected Fragment createItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                if (0 < getScrollY()) {
                    fragment = MemeChildFragment.newInstance(RecyclerFragment.SUPER_SLIM_LAYOUT, true, 1);
                } else {
                    fragment = MemeChildFragment.newInstance(RecyclerFragment.SUPER_SLIM_LAYOUT, true, 0);
                }
                break;
            case 1:
                if (0 < getScrollY()) {
                    fragment = MemeChildFragment.newInstance(RecyclerFragment.SUPER_SLIM_LAYOUT, true, 1);
                } else {
                    fragment = MemeChildFragment.newInstance(RecyclerFragment.SUPER_SLIM_LAYOUT, true, 0);
                }
                break;
            case 2:
                if (0 < getScrollY()) {
                    fragment = MemeChildFragment.newInstance(RecyclerFragment.SUPER_SLIM_LAYOUT, true, 1);
                } else {
                    fragment = MemeChildFragment.newInstance(RecyclerFragment.SUPER_SLIM_LAYOUT, true, 0);
                }
                break;
            default:
                if (0 < getScrollY()) {
                    fragment = MemeChildFragment.newInstance(RecyclerFragment.SUPER_SLIM_LAYOUT, true, 1);
                } else {
                    fragment = MemeChildFragment.newInstance(RecyclerFragment.SUPER_SLIM_LAYOUT, true, 0);
                }
                break;
        }
        return fragment;
    }

}
