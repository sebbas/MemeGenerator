package org.sebbas.android.memegenerator.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.fragments.ImgurChildFragment;

public class GalleryFragmentAdapter extends SlidingTabsAdapter {

    public GalleryFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    @Override
    protected Fragment createItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = ImgurChildFragment.newInstance(UIOptions.LIST_LAYOUT, true);
                break;
            case 1:
                fragment = ImgurChildFragment.newInstance(UIOptions.LIST_LAYOUT, true);
                break;
            case 2:
                fragment = ImgurChildFragment.newInstance(UIOptions.LIST_LAYOUT, true);
                break;
            case 3:
                fragment = ImgurChildFragment.newInstance(UIOptions.LIST_LAYOUT, true);
                break;
            default:
                fragment = ImgurChildFragment.newInstance(UIOptions.LIST_LAYOUT, true);
                break;
        }
        return fragment;
    }

}
