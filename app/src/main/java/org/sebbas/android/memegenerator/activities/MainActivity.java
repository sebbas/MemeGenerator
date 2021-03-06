package org.sebbas.android.memegenerator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.ToggleSwipeViewPager;
import org.sebbas.android.memegenerator.adapter.MainActivityAdapter;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.fragments.MemeFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.fragments.SlidingTabsFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements
        ObservableScrollViewCallbacks, FragmentCallback {

    private static final String TAG = "MainActivity";

    private static final int OFF_SCREEN_LIMIT = 5;
    private static final boolean IS_SWIPEABLE = false;
    private static final boolean IS_SMOOTH_SCROLL = true;
    private static final int ANIMATION_SPEED = 200;

    private int mLastFragmentPositionMain;
    private int mLastFragmentPositionSlidingTabs;

    private static final int[] TAB_TITLES = {
            R.string.memes,
            R.string.gifs,
            R.string.explore,
            R.string.gallery,
            R.string.chart};

    // Setup default first fragment tag
    private String mCurrentFragmentTag = MemeFragment.TAG;

    private ToggleSwipeViewPager mViewPager;
    private MainActivityAdapter mMainActivityAdapter;
    private View mHeaderView;
    private View mFooterView;
    private View mToolbarView;
    private int mBaseTranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This makes the actionbar in multitask mode in Android Lollipop look a bit nicer
        setCustomLollipopActionBar();

        mHeaderView = findViewById(R.id.header);
        mFooterView = findViewById(R.id.footer);
        mToolbarView = findViewById(R.id.toolbar);

        mMainActivityAdapter = new MainActivityAdapter(this, getSupportFragmentManager(), TAB_TITLES);

        mViewPager = (ToggleSwipeViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setPagingEnabled(IS_SWIPEABLE);
        mViewPager.setSmoothScrollEnabled(IS_SMOOTH_SCROLL);
        mViewPager.setAdapter(mMainActivityAdapter);
        mViewPager.setOffscreenPageLimit(OFF_SCREEN_LIMIT);

        SlidingTabLayout slidingTabLayoutMain = (SlidingTabLayout) findViewById(R.id.sliding_tabs_main_navigation);
        slidingTabLayoutMain.setCustomTabView(R.layout.tab_main, R.id.tabsText);
        slidingTabLayoutMain.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayoutMain.setDistributeEvenly(true);
        slidingTabLayoutMain.setViewPager(mViewPager);

        slidingTabLayoutMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (mMainActivityAdapter.getRegisteredFragment(position) != null) {
                    mLastFragmentPositionMain = position;

                    mCurrentFragmentTag = getFragmentAt(position).getFragmentTag();

                    MainActivity.super.setupToolbar(mCurrentFragmentTag);

                    setupSlidingTabs(getFragmentAt(position));
                    registerFragmentToolbarCallbacks(getFragmentAt(position));

                    // Make sure that toolbar is shown when other page is selected
                    showToolbar();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setup top sliding tabs
        SlidingTabLayout slidingTabLayoutTabs = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayoutTabs.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayoutTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayoutTabs.setDistributeEvenly(true);
        slidingTabLayoutTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mLastFragmentPositionSlidingTabs = position;

                // Close a previously opened search view
                MainActivity.super.closeSearchView();

                // Make sure that child fragments in slidingtabsfragment register toolbarcallback
                registerFragmentToolbarCallbacks(getCurrentMainPosition());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupSlidingTabs(BaseFragment baseFragment) {
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        // Setup viewpager for tabs layout
        if (baseFragment != null && baseFragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) baseFragment;

            // Set visibility
            slidingTabLayout.setVisibility(View.VISIBLE);

            // Set the pager
            ViewPager pager = slidingTabsFragment.getViewPager();
            slidingTabLayout.setViewPager(pager);

            // Set the tab strip to the last known position
            int currentTab = slidingTabsFragment.getLastPage();
            slidingTabLayout.refreshTabStrip(currentTab, 0);
        }

        if (baseFragment != null && baseFragment instanceof RecyclerFragment) {
            // Set visibility
            slidingTabLayout.setVisibility(View.GONE);
        }
    }

    private void registerFragmentToolbarCallbacks(int position) {
        this.registerFragmentToolbarCallbacks(getFragmentAt(position));
    }

    private void registerFragmentToolbarCallbacks(Fragment fragment) {
        if (fragment instanceof RecyclerFragment) {
            RecyclerFragment currentFragment = (RecyclerFragment) fragment;
            super.registerToolbarCallback(currentFragment);

        } else if (fragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) fragment;
            RecyclerFragment currentChildFragment = (RecyclerFragment) slidingTabsFragment.getCurrentFragment();
            super.registerToolbarCallback(currentChildFragment);
        }
    }

    private BaseFragment getFragmentAt(int position) {
        return  (BaseFragment) mMainActivityAdapter.getRegisteredFragment(position);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
            if (firstScroll) {
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);

            // Translate toolbar while scrolling
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);

            // Translate footer while scrolling
            ViewPropertyAnimator.animate(mFooterView).cancel();
            ViewHelper.setTranslationY(mFooterView, -headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return;
        }

        if (fragment instanceof RecyclerFragment) {

            View view = fragment.getView();
            if (view == null) {
                return;
            }

            // ObservableXxxViews have same API
            // but currently they don't have any common interfaces.
            adjustToolbar(scrollState, view);
        }

        if (fragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) fragment;

            // Skip destroyed or not created item
            Fragment childFragment = slidingTabsFragment.getCurrentFragment();
            if (childFragment == null) {
                return;
            }

            View view = childFragment.getView();
            if (view == null) {
                return;
            }

            // ObservableXxxViews have same API
            // but currently they don't have any common interfaces.
            adjustToolbar(scrollState, view);
        }
    }

    private void adjustToolbar(ScrollState scrollState, View view) {
        int toolbarHeight = mToolbarView.getHeight();
        final Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        int scrollY = scrollView.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
            showMainTabs();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
                hideMainTabs();
            } else {
                showToolbar();
                showMainTabs();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (toolbarIsShown() || toolbarIsHidden()) {
                // Toolbar is completely moved, so just keep its state
                // and propagate it to other pages
                propagateToolbarState(toolbarIsShown());
            } else {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
                showMainTabs();
            }
        }
    }

    private BaseFragment getCurrentFragment() {
        return (BaseFragment) mMainActivityAdapter.getRegisteredFragment(getCurrentMainPosition());
    }

    private int getCurrentMainPosition() {
        return mViewPager.getCurrentItem();
    }

    private void propagateToolbarState(boolean isShown) {

        Fragment fragment = getCurrentFragment();

        if (fragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) fragment;

            // Set scrollY for the active fragments
            for (int i = 0; i < slidingTabsFragment.getChildFragmentCount(); i++) {
                // Skip current item
                if (i == slidingTabsFragment.getCurrentPosition()) {
                    continue;
                }

                // Skip destroyed or not created item
                BaseFragment childFragment = (BaseFragment) slidingTabsFragment.getFragmentAt(i);
                if (childFragment == null) {
                    continue;
                }

                View view = childFragment.getView();
                if (view == null) {
                    continue;
                }
                propagateToolbarState(isShown, view, childFragment);
            }
        }
    }

    private void propagateToolbarState(boolean isShown, View view, BaseFragment fragment) {
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        if (recyclerView == null) {
            return;
        }

        if (isShown) {
            // Scroll up
            if (fragment instanceof RecyclerFragment && ((RecyclerFragment) fragment).getFirstVisibleItemPosition() == 1) {
                // Note: layoutmanager.scrollToPosition() does not work for some reason
                recyclerView.scrollVerticallyToPosition(0);
            }
        } else {
            // Scroll down (to hide padding)
            if (fragment instanceof RecyclerFragment && ((RecyclerFragment) fragment).getFirstVisibleItemPosition() == 0) {
                recyclerView.scrollVerticallyToPosition(1);
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        View headerView = findViewById(R.id.header);
        float headerTranslationY = ViewHelper.getTranslationY(headerView);
        if (headerTranslationY != 0) {
            animateView(headerView, 0, ANIMATION_SPEED);
        }
        propagateToolbarState(true);
    }

    private void hideToolbar() {
        View headerView = findViewById(R.id.header);
        float headerTranslationY = ViewHelper.getTranslationY(headerView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            animateView(headerView, -toolbarHeight, ANIMATION_SPEED);
        }
        propagateToolbarState(false);
    }

    private void showMainTabs() {
        View footerView = findViewById(R.id.footer);
        float headerTranslationY = ViewHelper.getTranslationY(footerView);
        if (headerTranslationY != 0) {
            animateView(footerView, 0, ANIMATION_SPEED);
        }
    }

    private void hideMainTabs() {
        View footerView = findViewById(R.id.footer);
        float headerTranslationY = ViewHelper.getTranslationY(footerView);
        int slidingTabLayoutHeight = mFooterView.getHeight();
        if (headerTranslationY != -slidingTabLayoutHeight) {
            animateView(footerView, slidingTabLayoutHeight, ANIMATION_SPEED);
        }
    }

    private void animateView(View view, int translationY, int duration) {
        ViewPropertyAnimator.animate(view).cancel();
        ViewPropertyAnimator.animate(view).translationY(translationY).setDuration(duration).start();
    }

    @Override
    public void onFragmentComplete(BaseFragment baseFragment) {

        if (baseFragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) baseFragment;
            int lastPosition = mLastFragmentPositionMain;
            int currentPosition = slidingTabsFragment.getPositionInParent();

            if (lastPosition == currentPosition) {
                setupSlidingTabs(baseFragment);
            }
        }

        if (baseFragment instanceof RecyclerFragment) {
            RecyclerFragment recyclerFragment = (RecyclerFragment) baseFragment;

            int lastParentPosition = 0;
            int currentParentPosition = 0;
            int lastPosition = 0;
            int currentPosition = 0;

            // If fragment has a parent (i.e. embedded in sliding tabs fragments)
            if (recyclerFragment.getParentFragment() != null) {
                lastParentPosition = mLastFragmentPositionMain;
                currentParentPosition = recyclerFragment.getParentPosition();

                lastPosition = mLastFragmentPositionSlidingTabs;
                currentPosition = recyclerFragment.getPositionInParent();
            } else {
                lastPosition = mLastFragmentPositionMain;
                currentPosition = recyclerFragment.getPositionInParent();
            }

            if (lastParentPosition == currentParentPosition && lastPosition == currentPosition) {
                registerToolbarCallback(recyclerFragment);
                setupToolbar(recyclerFragment.getFragmentTag());
            }
        }
    }

    @Override
    public void onItemClick(int contentPosition, ArrayList<LineItem> contentItems) {
        /*Intent editorIntent = new Intent(this, EditorActivity.class);
        editorIntent.putExtra(EditorActivity.START_POSITION, contentPosition);
        editorIntent.putExtra(EditorActivity.LINE_ITEMS, contentItems);
        startActivity(editorIntent);*/
        Intent editorIntent = new Intent(this, EditActivity.class);
        editorIntent.putExtra(EditActivity.LINE_ITEM, contentItems.get(contentPosition));
        startActivity(editorIntent);
    }

    public void bringMainNavigationToFront() {
        findViewById(R.id.header).bringToFront();
        findViewById(R.id.footer).bringToFront();
    }

    @Override
    public int getMainPagerPosition() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public int getLastFragmentPositionMain() {
        return mLastFragmentPositionMain;
    }

}