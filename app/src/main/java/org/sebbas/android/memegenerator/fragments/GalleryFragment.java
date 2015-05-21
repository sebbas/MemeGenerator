package org.sebbas.android.memegenerator.fragments;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.adapter.GalleryFragmentAdapter;
import org.sebbas.android.memegenerator.activities.MainActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.SlidingTabsFragmentAdapter;

public class GalleryFragment extends SlidingTabsFragment implements ToolbarCallback {

    private static final int OFF_SCREEN_LIMIT = 3;

    private static int[] TAB_TITLES = {
            R.string.my_memes,
            R.string.favorites,
            R.string.recent};

    public static GalleryFragment newInstance(Context context) {
        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        GalleryFragmentAdapter galleryFragmentAdapter = new GalleryFragmentAdapter(context,
                fragmentManager, TAB_TITLES);

        return new GalleryFragment(galleryFragmentAdapter, OFF_SCREEN_LIMIT, true);
    }

    private GalleryFragment(SlidingTabsFragmentAdapter adapter, int offScreenLimit, boolean isSwipeable) {
        super(adapter, offScreenLimit, isSwipeable);
    }

    @Override
    void setupFragmentToolbarAt(int position) {
        int titleResource = R.string.gallery;
        int menuResource = R.menu.menu_simple_fragment;

        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.setupToolbar(parentActivity, titleResource, menuResource, false);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onRefreshClicked() {

    }

    @Override
    public void onBackPressed() {

    }
}
