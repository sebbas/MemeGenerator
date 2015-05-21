package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.SlidingTabsFragmentAdapter;


public abstract class SlidingTabsFragment extends BaseFragment implements ObservableScrollViewCallbacks {

    private static final String TAG = "SlidingTabsFragment";

    private SlidingTabsFragmentAdapter mSlidingTabsFragmentAdapter;
    private int mOffScreenLimit;
    private boolean mIsSwipeable;
    private View mToolbar;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private ViewPager mViewPager;
    private int mSlop;
    private boolean mScrolled;
    private ScrollState mLastScrollState;

    public SlidingTabsFragment(SlidingTabsFragmentAdapter adapter, int offScreenLimit, boolean isSwipeable) {
        mSlidingTabsFragmentAdapter = adapter;
        mOffScreenLimit = offScreenLimit;
        mIsSwipeable = isSwipeable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view;

        if (mIsSwipeable) {
            view = inflater.inflate(R.layout.fragment_slidingtabs_swipeable, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_slidingtabs_nonswipeable, container, false);
        }

        mToolbar = getActivity().findViewById(R.id.toolbar);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSlidingTabsFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mOffScreenLimit);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.main_tabs);
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
                registerFragmentToolbarCallbacks(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ViewConfiguration vc = ViewConfiguration.get(getActivity());
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout = (TouchInterceptionFrameLayout) view.findViewById(R.id.container);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);

        // Setup toolbar for initial configuration
        setupFragmentToolbarAt(0);
        registerFragmentToolbarCallbacks(0);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.unregisterToolbarCallback();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (!mScrolled) {
            // This event can be used only when TouchInterceptionFrameLayout
            // doesn't handle the consecutive events.
            adjustToolbar(scrollState);
        }
    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener =
            new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = getCurrentScrollable();
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any longer.
            int toolbarHeight = mToolbar.getHeight();
            int translationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;
            if (scrollingUp) {
                if (translationY < 0) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.UP;
                    return true;
                }
            } else if (scrollingDown) {
                if (-toolbarHeight < translationY) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.DOWN;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -mToolbar.getHeight(), 0);
            ViewHelper.setTranslationY(mInterceptionLayout, translationY);
            if (translationY < 0) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                lp.height = (int) (-translationY + getScreenHeight());
                mInterceptionLayout.requestLayout();
            }
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            mScrolled = false;
            adjustToolbar(mLastScrollState);
        }
    };

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void adjustToolbar(ScrollState scrollState) {
        int toolbarHeight = mToolbar.getHeight();
        final Scrollable scrollable = getCurrentScrollable();
        if (scrollable == null) {
            return;
        }
        int scrollY = scrollable.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else if (!toolbarIsShown() && !toolbarIsHidden()) {
            // Toolbar is moving but doesn't know which to move:
            // you can change this to hideToolbar()
            //showToolbar();
            hideToolbar();
        }
    }

    private Fragment getCurrentFragment() {
        return mSlidingTabsFragmentAdapter.getItemAt(mViewPager.getCurrentItem());
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mInterceptionLayout) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mInterceptionLayout) == -mToolbar.getHeight();
    }

    private void showToolbar() {
        animateToolbar(0);
    }

    private void hideToolbar() {
        animateToolbar(-mToolbar.getHeight());
    }

    private void animateToolbar(final float toY) {
        float layoutTranslationY = ViewHelper.getTranslationY(mInterceptionLayout);
        if (layoutTranslationY != toY) {
            ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mInterceptionLayout), toY).setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float translationY = (float) animation.getAnimatedValue();
                    ViewHelper.setTranslationY(mInterceptionLayout, translationY);
                    if (translationY < 0) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                        lp.height = (int) (-translationY + getScreenHeight());
                        mInterceptionLayout.requestLayout();
                    }
                }
            });
            animator.start();
        }
    }

    abstract void setupFragmentToolbarAt(int position);

    void registerFragmentToolbarCallbacks(int position) {
        BaseFragment fragment = getFragmentAt(position);
        ((BaseActivity) getActivity()).registerToolbarCallback(fragment);
    }

    private BaseFragment getFragmentAt(int position) {
        return (BaseFragment) mSlidingTabsFragmentAdapter.instantiateItem(mViewPager, position);
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