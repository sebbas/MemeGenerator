package org.sebbas.android.memegenerator.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.fragments.SlidingTabsFragment;

public class ImgurFragmentAdapter extends SlidingTabsFragmentAdapter {

    private SlidingTabsFragment mParentFragment;

    public ImgurFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    public ImgurFragmentAdapter(SlidingTabsFragment parentFragment, Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
        mParentFragment = parentFragment;
    }

    @Override
    protected Fragment createItem(int position) {
        int id;
        int layout;
        Fragment fragment;

        switch (position) {
            case 0:
                id = RecyclerFragment.ALL;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            case 1:
                id = RecyclerFragment.MEMES;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            case 2:
                id = RecyclerFragment.GIFS;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            default:
                id = RecyclerFragment.ALL;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
        }
        return fragment;
    }

}
