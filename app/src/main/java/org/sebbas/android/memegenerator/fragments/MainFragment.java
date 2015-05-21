package org.sebbas.android.memegenerator.fragments;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.activities.MainActivity;
import org.sebbas.android.memegenerator.adapter.MainFragmentAdapter;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.SlidingTabsFragmentAdapter;

public class MainFragment extends SlidingTabsFragment {

    private static final int OFF_SCREEN_LIMIT = 5;

    private static int[] TAB_TITLES = {
            R.string.templates,
            R.string.imgur,
            R.string.explore,
            R.string.gallery,
            R.string.preferences};

    public static MainFragment newInstance(Context context) {
        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        MainFragmentAdapter mainFragmentAdapter = new MainFragmentAdapter(context,
                fragmentManager, TAB_TITLES);

        return new MainFragment(mainFragmentAdapter, OFF_SCREEN_LIMIT, false);
    }

    private MainFragment(SlidingTabsFragmentAdapter adapter, int offScreenLimit, boolean isSwipeable) {
        super(adapter, offScreenLimit, isSwipeable);
    }

    @Override
    void setupFragmentToolbarAt(int position) {
        int titleResource = 0;
        int menuResource = 0;

        switch(position) {
            case 0:
                titleResource = R.string.templates;
                menuResource = R.menu.menu_simple_fragment;
                break;
            case 1:
                titleResource = R.string.imgur;
                menuResource = R.menu.menu_sliding_tabs_fragment;
                break;
            case 2:
                titleResource = R.string.explore;
                break;
            case 3:
                titleResource = R.string.gallery;
                menuResource = R.menu.menu_sliding_tabs_fragment;
                break;
            case 4:
                titleResource = R.string.preferences;
                break;
            default:
                titleResource = R.string.app_name;
                menuResource = R.menu.menu_simple_fragment;
                break;
        }
        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.setupToolbar(parentActivity, titleResource, menuResource, false);
    }
}
