package org.sebbas.android.memegenerator.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
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
import org.sebbas.android.memegenerator.fragments.EditorFragment;
import org.sebbas.android.memegenerator.fragments.MemeFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.fragments.SlidingTabsFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;
import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;

import java.util.List;

public class MainActivity extends BaseActivity implements ItemClickCallback,
        ObservableScrollViewCallbacks, FragmentCallback {

    private static final String TAG = "MainActivity";
    private static final int OFF_SCREEN_LIMIT = 5;
    private static final boolean IS_SWIPEABLE = false;
    private static final int FIRST_FRAGMENT_POSITION = 0;

    private static int[] TAB_TITLES = {
            R.string.templates,
            R.string.gifs,
            R.string.Editor,
            R.string.gallery,
            R.string.preferences};

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

        mViewPager = (ToggleSwipeViewPager) findViewById(R.id.toogle_swipe_viewpager);
        mViewPager.setPagingEnabled(IS_SWIPEABLE);
        mViewPager.setAdapter(mMainActivityAdapter);
        mViewPager.setOffscreenPageLimit(OFF_SCREEN_LIMIT);

        SlidingTabLayout slidingTabLayoutMain = (SlidingTabLayout) findViewById(R.id.sliding_tabs_main_navigation);
        slidingTabLayoutMain.setCustomTabView(R.layout.tab_main, 0);
        slidingTabLayoutMain.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayoutMain.setDistributeEvenly(true);
        slidingTabLayoutMain.setViewPager(mViewPager);

        slidingTabLayoutMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setupFragmentToolbarAt(position);
                setupSlidingTabsAt(position);
                registerFragmentToolbarCallbacks(position);

                // Make sure that toolbar is shown when other page is selected
                showToolbar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Setup top sliding tabs
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        mSlidingTabLayout.setDistributeEvenly(true);

        // Setup toolbar and sliding tabs for initial fragment in viewpager
        setupFragmentToolbarAt(FIRST_FRAGMENT_POSITION);
        registerFragmentToolbarCallbacks(FIRST_FRAGMENT_POSITION);
    }

    @Override
    public void onItemClick(int position, List<LineItem> lineItems) {
        LineItem lineItem = lineItems.get(position);
        String imageUrl = lineItem.getImageUrl();
        String imageId = lineItem.getImageId();
        int imageWidth = lineItem.getImageWidth();
        int imageHeight = lineItem.getImageHeight();

        EditorFragment editorFragment = EditorFragment.newInstance();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.add(R.id.main_container, editorFragment, EditorFragment.class.getName());
        fragmentTransaction.addToBackStack(EditorFragment.class.getName());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
        }
    }

    private void setupFragmentToolbarAt(int position) {
        int titleResource = 0;
        int menuResource = 0;

        switch(position) {
            case 0:
                titleResource = R.string.templates;
                menuResource = R.menu.menu_simple_fragment;
                break;
            case 1:
                titleResource = R.string.gifs;
                menuResource = R.menu.menu_sliding_tabs_fragment;
                break;
            case 2:
                titleResource = R.string.Editor;
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
        setupToolbar(titleResource, menuResource, false);
    }

    private void setupSlidingTabsAt(int position) {
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        switch(position) {
            case 0:
                slidingTabLayout.setVisibility(View.VISIBLE);
                setSlidingTabsViewPagerAt(position);
                break;
            case 1:
                slidingTabLayout.setVisibility(View.VISIBLE);
                setSlidingTabsViewPagerAt(position);
                break;
            case 2:
                slidingTabLayout.setVisibility(View.GONE);
                break;
            case 3:
                slidingTabLayout.setVisibility(View.VISIBLE);
                setSlidingTabsViewPagerAt(position);
                break;
            case 4:
                slidingTabLayout.setVisibility(View.GONE);
                break;
            default:
                slidingTabLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void setSlidingTabsViewPagerAt(int position) {
        SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) getFragmentAt(position);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        ViewPager pager = slidingTabsFragment.getViewPager();
        slidingTabLayout.setViewPager(pager);
    }

    private void registerFragmentToolbarCallbacks(int position) {
        BaseFragment fragment = getFragmentAt(position);
        registerToolbarCallback(fragment);
    }

    private BaseFragment getFragmentAt(int position) {
        return (BaseFragment) mMainActivityAdapter.instantiateItem(mViewPager, position);
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
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
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
        return (BaseFragment) mMainActivityAdapter.getItemAt(mViewPager.getCurrentItem());
    }

    private void propagateToolbarState(boolean isShown) {

        Fragment fragment = getCurrentFragment();

        if (fragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) fragment;

            // Set scrollY for the fragments that are not created yet
            slidingTabsFragment.getSlidingTabsAdapter().setScrollY(isShown ? 0 : 1);

            // Set scrollY for the active fragments
            for (int i = 0; i < slidingTabsFragment.getChildFragmentCount(); i++) {
                // Skip current item
                if (i == slidingTabsFragment.getCurrentPosition()) {
                    continue;
                }

                // Skip destroyed or not created item
                RecyclerFragment childFragment = slidingTabsFragment.getFragmentAt(i);
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

    private void propagateToolbarState(boolean isShown, View view, RecyclerFragment fragment) {
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        if (recyclerView == null) {
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (isShown) {
            // Scroll up
            if (fragment.getFirstVisibleItemPosition() == 1) {
                layoutManager.scrollToPosition(0);
            }
        } else {
            // Scroll down (to hide padding)
            if (fragment.getFirstVisibleItemPosition() == 0) {
                layoutManager.scrollToPosition(1);
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
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
        propagateToolbarState(true);
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
        propagateToolbarState(false);
    }

    private void showMainTabs() {
        float headerTranslationY = ViewHelper.getTranslationY(mFooterView);
        if (headerTranslationY != 0) {
            animateView(mFooterView, 0, 200);
        }
    }

    private void hideMainTabs() {
        float headerTranslationY = ViewHelper.getTranslationY(mFooterView);
        int slidingTabLayoutHeight = mFooterView.getHeight();
        if (headerTranslationY != -slidingTabLayoutHeight) {
            animateView(mFooterView, slidingTabLayoutHeight, 200);
        }
    }

    private void animateView(View view, int translation, int duration) {
        ViewPropertyAnimator.animate(view).cancel();
        ViewPropertyAnimator.animate(view).translationY(translation).setDuration(duration).start();
    }

    @Override
    public void onFragmentComplete(String fragmentTag) {

        if (fragmentTag.equals(getCurrentFragment().getFragmentTag())) {
            setupSlidingTabsAt(FIRST_FRAGMENT_POSITION);
        }
    }
}