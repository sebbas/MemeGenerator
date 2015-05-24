package org.sebbas.android.memegenerator.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.fragments.SlidingTabsChildFragment;

public class GalleryFragmentAdapter extends SlidingTabsFragmentAdapter {

    public GalleryFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
    }

    @Override
    protected Fragment createItem(int position) {
        int id;
        int layout;
        Fragment fragment;

        switch (position) {
            case 0:
                id = RecyclerFragment.MY_MEMES;
                layout = UIOptions.getLayoutMode(id);
                fragment = SlidingTabsChildFragment.newInstance(id, layout, true);
                break;
            case 1:
                id = RecyclerFragment.RECENT;
                layout = UIOptions.getLayoutMode(id);
                fragment = SlidingTabsChildFragment.newInstance(id, layout, true);
                break;
            case 2:
                id = RecyclerFragment.FAVORITE_TEMPLATES;
                layout = UIOptions.getLayoutMode(id);
                fragment = SlidingTabsChildFragment.newInstance(id, layout, true);
                break;
            case 3:
                id = RecyclerFragment.FAVORITE_INSTANCES;
                layout = UIOptions.getLayoutMode(id);
                fragment = SlidingTabsChildFragment.newInstance(id, layout, true);
                break;
            default:
                id = RecyclerFragment.ALL;
                layout = UIOptions.getLayoutMode(id);
                fragment = SlidingTabsChildFragment.newInstance(id, layout, true);
                break;
        }
        return fragment;
    }

}
