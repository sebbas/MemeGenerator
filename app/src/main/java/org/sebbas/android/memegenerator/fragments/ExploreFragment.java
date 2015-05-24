package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.UIOptions;

public class ExploreFragment extends RecyclerFragment implements ToolbarCallback {

    private static final String TAG = "ExploreFragment";

    private static final int FRAGMENT_TYPE = RecyclerFragment.EXPLORE;
    private static final int LAYOUT_MODE = UIOptions.SCROLLBOX_LAYOUT;
    private static final boolean IS_REFRESHABLE = false;

    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_type", FRAGMENT_TYPE);
        args.putInt("layout_mode", LAYOUT_MODE);
        args.putBoolean("refreshable", IS_REFRESHABLE);
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
        int titleResource = R.string.explore;
        int menuResource = R.menu.menu_simple_fragment;

        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.setupToolbar(titleResource, menuResource, false);
    }

    @Override
    void registerFragmentToolbarCallbacks(int position) {
        ((BaseActivity) getActivity()).registerToolbarCallback(this);
    }
}
