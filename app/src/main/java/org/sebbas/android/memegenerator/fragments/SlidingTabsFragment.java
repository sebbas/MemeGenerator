package org.sebbas.android.memegenerator.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.sebbas.android.memegenerator.ToggleSwipeViewPager;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.SlidingTabsFragmentAdapter;

import java.lang.reflect.Field;


public abstract class SlidingTabsFragment extends BaseFragment implements ObservableScrollViewCallbacks {

    private static final String TAG = "SlidingTabsFragment";

    private SlidingTabsFragmentAdapter mSlidingTabsFragmentAdapter;
    private ToggleSwipeViewPager mViewPager;
    private View mToolbarView;
    private FragmentManager mRetainedChildFragmentManager;

    private int mBaseTranslationY;
    private SlidingTabLayout mSlidingTabLayout;


    public void createView(View view, SlidingTabsFragmentAdapter fragmentAdapter,
                           boolean isSwipeable, int offScreenLimit) {

        mToolbarView = getActivity().findViewById(R.id.toolbar);

        mSlidingTabsFragmentAdapter = fragmentAdapter;
        mViewPager = (ToggleSwipeViewPager) view.findViewById(R.id.toogle_swipe_viewpager);

        mViewPager.setPagingEnabled(isSwipeable);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setOffscreenPageLimit(offScreenLimit);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setupFragmentToolbarAt(position);
                registerFragmentToolbarCallbacks(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Setup toolbar for initial configuration
        setupFragmentToolbarAt(0);
        registerFragmentToolbarCallbacks(0);
    }

    public FragmentManager getRetainedChildFragmentManager() {
        if(mRetainedChildFragmentManager == null) {
            mRetainedChildFragmentManager = getChildFragmentManager();
        }
        return mRetainedChildFragmentManager;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mRetainedChildFragmentManager != null) {
            //restore the last retained child fragment manager to the new
            //created fragment
            try {
                Field childFMField = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFMField.setAccessible(true);
                childFMField.set(this, mRetainedChildFragmentManager);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.unregisterToolbarCallback();
    }



    @Override
    void registerFragmentToolbarCallbacks(int position) {
        BaseFragment fragment = getFragmentAt(position);
        ((BaseActivity) getActivity()).registerToolbarCallback(fragment);
    }

    private BaseFragment getFragmentAt(int position) {
        return (BaseFragment) mSlidingTabsFragmentAdapter.instantiateItem(mViewPager, position);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            if (firstScroll) {
                float currentHeaderTranslationY = ViewHelper.getTranslationY(mToolbarView);
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
            ViewHelper.setTranslationY(mSlidingTabLayout, headerTranslationY);

            ViewPropertyAnimator.animate(mToolbarView).cancel();
            ViewHelper.setTranslationY(mToolbarView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        RecyclerFragment currentFragmentInPager = (RecyclerFragment) getFragmentAt(mViewPager.getCurrentItem());
        ObservableRecyclerView recyclerView = currentFragmentInPager.getRecyclerView();

        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            int toolbarHeight = mToolbarView.getHeight();
            int scrollY = recyclerView.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (!toolbarIsShown() && !toolbarIsHidden()) {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mToolbarView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mToolbarView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mToolbarView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
            ViewPropertyAnimator.animate(mSlidingTabLayout).translationY(0).setDuration(200).start();

            ViewPropertyAnimator.animate(mToolbarView).cancel();
            ViewPropertyAnimator.animate(mToolbarView).translationY(0).setDuration(200).start();
        }
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mToolbarView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
            ViewPropertyAnimator.animate(mSlidingTabLayout).translationY(-toolbarHeight).setDuration(200).start();

            ViewPropertyAnimator.animate(mToolbarView).cancel();
            ViewPropertyAnimator.animate(mToolbarView).translationY(-toolbarHeight).setDuration(200).start();
        }
    }

    /*
     * Toolbar Callbacks
     */
    /*@Override
    public boolean onQueryTextSubmit(String s) {
        //int fragmentId = mViewPager.getCurrentItem();
        //Fragment visibleFragment = getChildFragmentManager().findFragmentById(fragmentId);
        //AdapterFilterListener adapterFilterListener = (AdapterFilterListener) visibleFragment;
        //adapterFilterListener.filterAdapterWith(s);
        //return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return true;
    }

    @Override
    public void onRefreshClicked() {
        int position = mViewPager.getCurrentItem();
        BaseFragment baseFragment = (BaseFragment) mSlidingTabsFragmentAdapter
                .instantiateItem(mViewPager, position);
        RecyclerViewListener recyclerViewListener = (RecyclerViewListener) baseFragment;
        recyclerViewListener.refreshAdapter();

    }*/
}