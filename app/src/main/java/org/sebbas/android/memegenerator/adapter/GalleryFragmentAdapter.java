package org.sebbas.android.memegenerator.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.fragments.GalleryChildFragmentOne;
import org.sebbas.android.memegenerator.fragments.GalleryChildFragmentThree;
import org.sebbas.android.memegenerator.fragments.GalleryChildFragmentTwo;
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
                fragment = GalleryChildFragmentOne.newInstance(RecyclerFragment.LIST_LAYOUT, true, position);
                break;
            case 1:
                fragment = GalleryChildFragmentTwo.newInstance(RecyclerFragment.LIST_LAYOUT, true, position);
                break;
            case 2:
                fragment = GalleryChildFragmentThree.newInstance(RecyclerFragment.LIST_LAYOUT, true, position);
                break;
            default:
                fragment = GalleryChildFragmentOne.newInstance(RecyclerFragment.LIST_LAYOUT, true, position);
        }
        return fragment;
    }

}
