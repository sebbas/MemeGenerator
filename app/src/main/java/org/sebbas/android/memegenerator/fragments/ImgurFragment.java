package org.sebbas.android.memegenerator.fragments;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.adapter.ImgurFragmentAdapter;
import org.sebbas.android.memegenerator.activities.MainActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.SlidingTabsFragmentAdapter;

public class ImgurFragment extends SlidingTabsFragment implements ToolbarCallback {

    private static final int OFF_SCREEN_LIMIT = 3;

    private static int[] TAB_TITLES = {
            R.string.all_images,
            R.string.memes,
            R.string.gifs};

    public static ImgurFragment newInstance(Context context) {
        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        ImgurFragmentAdapter imgurFragmentAdapter = new ImgurFragmentAdapter(context,
                fragmentManager, TAB_TITLES);

        return new ImgurFragment(imgurFragmentAdapter, OFF_SCREEN_LIMIT, true);
    }

    private ImgurFragment(SlidingTabsFragmentAdapter adapter, int offScreenLimit, boolean isSwipeable) {
        super(adapter, offScreenLimit, isSwipeable);
    }

    @Override
    void setupFragmentToolbarAt(int position) {
        int titleResource = R.string.imgur;
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
