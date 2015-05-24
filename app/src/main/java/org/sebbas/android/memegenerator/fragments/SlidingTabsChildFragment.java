package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public class SlidingTabsChildFragment extends RecyclerFragment implements ToolbarCallback {

    private static final String TAG = "SlidingTabsChildFragment";

    public static SlidingTabsChildFragment newInstance(int fragmentType, int layoutMode, boolean isRefreshable) {
        SlidingTabsChildFragment fragment = new SlidingTabsChildFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_type", fragmentType);
        args.putInt("layout_mode", layoutMode);
        args.putBoolean("refreshable", isRefreshable);
        fragment.setArguments(args);
        return fragment;
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

    @Override
    void setupFragmentToolbarAt(int position) {
        int titleResource = R.string.templates;
        int menuResource = R.menu.menu_simple_fragment;

        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.setupToolbar(titleResource, menuResource, true);
    }

    @Override
    void registerFragmentToolbarCallbacks(int position) {
        ((BaseActivity) getActivity()).registerToolbarCallback(this);
    }
}
