package org.sebbas.android.memegenerator.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.fragments.GifChildFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

public class GalleryFragmentAdapter extends SlidingTabsAdapter {

    public GalleryFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    @Override
    protected Fragment createItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = GifChildFragment.newInstance(RecyclerFragment.LIST_LAYOUT, true, getScrollY());
                break;
            case 1:
                fragment = GifChildFragment.newInstance(RecyclerFragment.LIST_LAYOUT, true, getScrollY());
                break;
            case 2:
                fragment = GifChildFragment.newInstance(RecyclerFragment.LIST_LAYOUT, true, getScrollY());
                break;
            case 3:
                fragment = GifChildFragment.newInstance(RecyclerFragment.LIST_LAYOUT, true, getScrollY());
                break;
            default:
                fragment = GifChildFragment.newInstance(RecyclerFragment.LIST_LAYOUT, true, getScrollY());
                break;
        }
        return fragment;
    }

}
