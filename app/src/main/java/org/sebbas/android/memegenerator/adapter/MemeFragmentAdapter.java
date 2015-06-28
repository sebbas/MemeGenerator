package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

public class MemeFragmentAdapter extends SlidingTabsAdapter {

    private static final String TAG = "MemeFragmentAdapter";

    public MemeFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = RecyclerFragment.newInstance(RecyclerFragment.MEME_FRAGMENT_ONE, RecyclerFragment.SUPER_SLIM_LAYOUT, false, position);
                break;
            case 1:
                fragment = RecyclerFragment.newInstance(RecyclerFragment.MEME_FRAGMENT_TWO, RecyclerFragment.SUPER_SLIM_LAYOUT, false, position);
                break;
            case 2:
                fragment = RecyclerFragment.newInstance(RecyclerFragment.MEME_FRAGMENT_THREE, RecyclerFragment.SUPER_SLIM_LAYOUT, false, position);
                break;
            default:
                fragment = RecyclerFragment.newInstance(RecyclerFragment.MEME_FRAGMENT_ONE, RecyclerFragment.SUPER_SLIM_LAYOUT, false, position);
        }
        return fragment;
    }
}
