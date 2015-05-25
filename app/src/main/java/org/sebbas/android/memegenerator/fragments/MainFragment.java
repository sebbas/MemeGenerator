package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;

import org.sebbas.android.memegenerator.ToggleSwipeViewPager;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.adapter.MainActivityAdapter;
import org.sebbas.android.memegenerator.R;

public class MainFragment extends BaseFragment {

    private static final int OFF_SCREEN_LIMIT = 5;
    private static final boolean IS_SWIPEABLE = false;

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

    private MainActivityAdapter mMainActivityAdapter;
    private ToggleSwipeViewPager mViewPager;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        mMainActivityAdapter = new MainActivityAdapter(getActivity(), fragmentManager, TAB_TITLES_2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        View view = inflater.inflate(R.layout.fragment_slidingtabs, container, false);

        mViewPager = (ToggleSwipeViewPager) view.findViewById(R.id.toogle_swipe_viewpager);

        mViewPager.setPagingEnabled(IS_SWIPEABLE);
        mViewPager.setAdapter(mMainActivityAdapter);
        mViewPager.setOffscreenPageLimit(OFF_SCREEN_LIMIT);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) getActivity().findViewById(R.id.sliding_tabs_main_navigation);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mViewPager);

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setupFragmentToolbarAt(position);
                setupSlidingTabsAt(position);
                registerFragmentToolbarCallbacks(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Setup toolbar and sliding tabs for initial fragment in viewpager
        setupFragmentToolbarAt(0);
        setupSlidingTabsAt(0);
        registerFragmentToolbarCallbacks(0);

        return view;
    }

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
        parentActivity.setupToolbar(titleResource, menuResource, false);
    }

    void setupSlidingTabsAt(int position) {
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) getActivity().findViewById(R.id.sliding_tabs);
        switch(position) {
            case 0:
                slidingTabLayout.setVisibility(View.GONE);
                break;
            case 1:
                slidingTabLayout.setVisibility(View.VISIBLE);
                ImgurFragment imgurFragment =  (ImgurFragment) getFragmentAt(position);
                break;
            case 2:
                slidingTabLayout.setVisibility(View.GONE);
                break;
            case 3:
                slidingTabLayout.setVisibility(View.VISIBLE);
                break;
            case 4:
                slidingTabLayout.setVisibility(View.GONE);
                break;
            default:
                slidingTabLayout.setVisibility(View.GONE);
                break;
        }
    }

    void registerFragmentToolbarCallbacks(int position) {
        BaseFragment fragment = getFragmentAt(position);
        ((BaseActivity) getActivity()).registerToolbarCallback(fragment);
    }

    private BaseFragment getFragmentAt(int position) {
        return (BaseFragment) mMainActivityAdapter.instantiateItem(mViewPager, position);
    }
}