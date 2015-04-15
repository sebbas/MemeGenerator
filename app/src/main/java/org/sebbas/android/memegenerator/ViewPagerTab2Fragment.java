package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Another implementation of ViewPagerTabActivity.
 * This uses TouchInterceptionFrameLayout to move Fragments.
 * <p/>
 * SlidingTabLayout and SlidingTabStrip are from google/iosched:
 * https://github.com/google/iosched
 */
public class ViewPagerTab2Fragment extends BaseFragment implements ObservableScrollViewCallbacks {

    private View mToolbarView;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private int mSlop;
    private boolean mScrolled;
    private ScrollState mLastScrollState;
    private ActionBarActivity mParentActivity;

    public static ViewPagerTab2Fragment newInstance() {
        return new ViewPagerTab2Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = (ActionBarActivity) getActivity();
    }

    @Override
    public void updateAdapter() {
        mPagerAdapter = new NavigationAdapter(mParentActivity.getSupportFragmentManager());
        int previousPagerPosition = mPager.getCurrentItem();

        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(previousPagerPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_viewpagertab2, container, false);

        mParentActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        ViewCompat.setElevation(view.findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = view.findViewById(R.id.toolbar);
        mPagerAdapter = new NavigationAdapter(mParentActivity.getSupportFragmentManager());
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange.
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        view.findViewById(R.id.pager_wrapper).setPadding(0, getActionBarSize() + tabHeight, 0, 0);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);

        ViewConfiguration vc = ViewConfiguration.get(mParentActivity);
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout = (TouchInterceptionFrameLayout) view.findViewById(R.id.container);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);

        return view;
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

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
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
            int toolbarHeight = mToolbarView.getHeight();
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
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -mToolbarView.getHeight(), 0);
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
        int toolbarHeight = mToolbarView.getHeight();
        final Scrollable scrollable = getCurrentScrollable();
        if (scrollable == null) {
            return;
        }
        int scrollY = scrollable.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            /*if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }*/
            hideToolbar();
        } else if (!toolbarIsShown() && !toolbarIsHidden()) {
            // Toolbar is moving but doesn't know which to move:
            // you can change this to hideToolbar()
            //showToolbar();
            hideToolbar();
        }
    }

    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mInterceptionLayout) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mInterceptionLayout) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        animateToolbar(0);
    }

    private void hideToolbar() {
        animateToolbar(-mToolbarView.getHeight());
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

    public static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private static final String[] TITLES = new String[] {"All", "Trending", "Popular", "New", "Favorites"};

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        protected Fragment createItem(int position) {
            BaseFragment f;
            switch (position) {
                case 0:
                    f = ViewPagerTab2GridViewFragment.newInstance(Data.URL_TRENDING);
                    break;
                case 1:
                    f = ViewPagerTab2GridViewFragment.newInstance(Data.URL_TRENDING);
                    break;
                case 2:
                    f = ViewPagerTab2GridViewFragment.newInstance(Data.URL_POPULAR);
                    break;
                case 3:
                    f = ViewPagerTab2GridViewFragment.newInstance(Data.URL_NEW);
                    break;
                case 4:
                    f = ViewPagerTab2GridViewFragment.newInstance(Data.URL_NEW);
                    break;
                default:
                    f = ViewPagerTab2GridViewFragment.newInstance(Data.URL_TRENDING);
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}