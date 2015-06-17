package org.sebbas.android.memegenerator.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import org.sebbas.android.memegenerator.fragments.EditorFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.fragments.SlidingTabsFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;
import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ItemClickCallback,
        ObservableScrollViewCallbacks, FragmentCallback {

    private static final String TAG = "MainActivity";
    private static final int OFF_SCREEN_LIMIT = 5;
    private static final boolean IS_SWIPEABLE = false;
    private static final boolean IS_SMOOTH_SCROLL = false;

    private static final int[] LAST_FRAGMENT_POSITIONS = new int[2];
    private static final int[] TAB_TITLES = {
            R.string.memes,
            R.string.gifs,
            R.string.editor,
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
        mViewPager.setSmoothScrollEnabled(IS_SMOOTH_SCROLL);
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
                LAST_FRAGMENT_POSITIONS[0] = position;
                setupFragmentToolbarAt(position);
                setupSlidingTabsAt(position, getFragmentAt(position));
                registerFragmentToolbarCallbacks(position);

                // Make sure that toolbar is shown when other page is selected
                showToolbar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setup top sliding tabs
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                LAST_FRAGMENT_POSITIONS[1] = position;

                // Close a previously opened search view
                MainActivity.super.closeSearchView();

                // Make sure that child fragments in slidingtabsfragment register toolbarcallback
                registerFragmentToolbarCallbacks(mViewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setup toolbar for initial fragment in viewpager
        setupFragmentToolbarAt(getCurrentPosition());
    }

    @Override
    public void onItemClick(int position, List<LineItem> lineItems) {

        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<Integer> imageWidths = new ArrayList<>();
        ArrayList<Integer> imageHeights = new ArrayList<>();
        for (LineItem lineItem : lineItems) {
            try {
                String url = lineItem.getImageUrl();
                imageUrls.add(url);

                int width = lineItem.getImageWidth();
                imageWidths.add(width);

                int height = lineItem.getImageHeight();
                imageHeights.add(height);
            } catch (Exception e) {

            }
        }

        EditorFragment editorFragment = EditorFragment.newInstance(position, imageUrls, imageWidths, imageHeights);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.add(R.id.main_container, editorFragment, EditorFragment.class.getName());
        fragmentTransaction.addToBackStack(EditorFragment.class.getName());
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
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
                titleResource = R.string.memes;
                menuResource = R.menu.menu_memes;
                break;
            case 1:
                titleResource = R.string.gifs;
                menuResource = R.menu.menu_gifs;
                break;
            case 2:
                titleResource = R.string.editor;
                break;
            case 3:
                titleResource = R.string.gallery;
                menuResource = R.menu.menu_gallery;
                break;
            case 4:
                titleResource = R.string.preferences;
                break;
            default:
                titleResource = R.string.app_name;
                menuResource = R.menu.menu_memes;
                break;
        }
        setupToolbar(titleResource, menuResource, false);
    }

    private void setupSlidingTabsAt(int position, Fragment fragment) {
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        // Setup visibility of tabs layout
        switch(position) {
            case 0:
                slidingTabLayout.setVisibility(View.VISIBLE);
                break;
            case 1:
                slidingTabLayout.setVisibility(View.VISIBLE);
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

        // Setup viewpager for tabs layout
        if (fragment != null && fragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) fragment;

            // Set the pager
            ViewPager pager = slidingTabsFragment.getViewPager();
            slidingTabLayout.setViewPager(pager);

            // Set the tab strip to the last known position
            int currentTab = slidingTabsFragment.getLastPage();
            slidingTabLayout.refreshTabStrip(currentTab, 0);
        }
    }

    private void registerFragmentToolbarCallbacks(int position) {
        this.registerFragmentToolbarCallbacks(getFragmentAt(position));
    }

    private void registerFragmentToolbarCallbacks(Fragment fragment) {
        if (fragment instanceof RecyclerFragment) {
            super.registerToolbarCallback(fragment);

        } else if (fragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) fragment;
            BaseFragment currentChildFragment = (BaseFragment) slidingTabsFragment.getCurrentFragment();
            super.registerToolbarCallback(currentChildFragment);
        }
    }

    private Fragment getFragmentAt(int position) {
        return  mMainActivityAdapter.getRegisteredFragment(position); //(BaseFragment) mMainActivityAdapter.instantiateItem(mViewPager, position);
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

    private Fragment getCurrentFragment() {
        return mMainActivityAdapter.getRegisteredFragment(getCurrentPosition());
    }

    private int getCurrentPosition() {
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
                RecyclerFragment childFragment = (RecyclerFragment) slidingTabsFragment.getFragmentAt(i);
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


        if (isShown) {
            // Scroll up
            if (fragment.getFirstVisibleItemPosition() == 1) {
                // Note: layoutmanager.scrollToPosition() does not work for some reason
                recyclerView.scrollVerticallyToPosition(0);
            }
        } else {
            // Scroll down (to hide padding)
            if (fragment.getFirstVisibleItemPosition() == 0) {
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

    private void animateView(View view, int translationY, int duration) {
        ViewPropertyAnimator.animate(view).cancel();
        ViewPropertyAnimator.animate(view).translationY(translationY).setDuration(duration).start();
    }

    @Override
    public void onFragmentComplete(BaseFragment baseFragment) {

        if (baseFragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) baseFragment;
            int lastPosition = LAST_FRAGMENT_POSITIONS[0];
            int fragmentPosition = slidingTabsFragment.getPositionInParent();
            if (lastPosition == fragmentPosition) {
                setupSlidingTabsAt(fragmentPosition, baseFragment);
            }
        }

        if (baseFragment instanceof RecyclerFragment) {
            RecyclerFragment recyclerFragment = (RecyclerFragment) baseFragment;
            int lastParentPosition = LAST_FRAGMENT_POSITIONS[0];
            int fragmentParentPosition = recyclerFragment.getParentPosition();

            int lastPosition = LAST_FRAGMENT_POSITIONS[1];
            int fragmentPosition = recyclerFragment.getPositionInParent();

            if (lastParentPosition == fragmentParentPosition && lastPosition == fragmentPosition) {
                registerToolbarCallback(recyclerFragment);
            }
        }
    }
}