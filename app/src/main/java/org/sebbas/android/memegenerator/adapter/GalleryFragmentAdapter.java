package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

public class GalleryFragmentAdapter extends SlidingTabsAdapter {

    private static final String TAG = "GalleryFragmentAdapter";

    public GalleryFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = RecyclerFragment.newInstance(
                        RecyclerFragment.GALLERY_FRAGMENT_ONE,
                        RecyclerFragment.LIST_LAYOUT,
                        RecyclerFragment.CARD,
                        false, position);
                break;
            case 1:
                fragment = RecyclerFragment.newInstance(
                        RecyclerFragment.GALLERY_FRAGMENT_TWO,
                        RecyclerFragment.LIST_LAYOUT,
                        RecyclerFragment.CARD,
                        false, position);
                break;
            case 2:
                fragment = RecyclerFragment.newInstance(
                        RecyclerFragment.GALLERY_FRAGMENT_THREE,
                        RecyclerFragment.LIST_LAYOUT,
                        RecyclerFragment.CARD,
                        false, position);
                break;
            default:
                fragment = RecyclerFragment.newInstance(
                        RecyclerFragment.GALLERY_FRAGMENT_ONE,
                        RecyclerFragment.LIST_LAYOUT,
                        RecyclerFragment.CARD,
                        false, position);
        }
        return fragment;
    }

}
