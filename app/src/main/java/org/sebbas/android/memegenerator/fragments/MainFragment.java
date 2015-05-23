package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.sebbas.android.memegenerator.ToggleSwipeViewPager;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.adapter.MainFragmentAdapter;
import org.sebbas.android.memegenerator.R;

public class MainFragment extends SlidingTabsFragment implements ObservableScrollViewCallbacks {

    private static final int OFF_SCREEN_LIMIT = 5;

    private static int[] TAB_TITLES = {
            R.string.templates,
            R.string.imgur,
            R.string.explore,
            R.string.gallery,
            R.string.preferences};

    private static int[] TAB_TITLES_2 = {
            R.string.a,
            R.string.b,
            R.string.c,
            R.string.d,
            R.string.e};

    private static final int[] TAB_ICONS = {
            R.drawable.selector_template_icon,
            R.drawable.selector_instances_icon,
            R.drawable.selector_explore_icon,
            R.drawable.selector_gallery_icon,
            R.drawable.selector_preferences_icon};

    private MainFragmentAdapter mMainFragmentAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        mMainFragmentAdapter = new MainFragmentAdapter(getActivity(), fragmentManager, TAB_TITLES_2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        View view = inflater.inflate(R.layout.fragment_slidingtabs_bottom, container, false);

        super.createView(view, mMainFragmentAdapter, false, OFF_SCREEN_LIMIT);

        return view;
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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}
