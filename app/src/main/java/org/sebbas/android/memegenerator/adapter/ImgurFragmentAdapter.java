package org.sebbas.android.memegenerator.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.fragments.ImgurChildFragment;

public class ImgurFragmentAdapter extends SlidingTabsAdapter {

    private static final String TAG = "ImgurFragmentAdapter";

    public ImgurFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
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
            default:
                fragment = ImgurChildFragment.newInstance(UIOptions.LIST_LAYOUT, true);
                break;
        }
        return fragment;
    }

}
