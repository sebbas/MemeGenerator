package org.sebbas.android.memegenerator.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.tonicartos.superslim.LayoutManager;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.ToggleSwipeViewPager;
import org.sebbas.android.memegenerator.adapter.MainActivityAdapter;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.fragments.EditorFragment;
import org.sebbas.android.memegenerator.fragments.GifFragment;
import org.sebbas.android.memegenerator.fragments.MemeFragment;
import org.sebbas.android.memegenerator.fragments.SlidingTabsFragment;
import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;

import java.util.List;

public class MainActivity extends BaseActivity implements ItemClickCallback, ObservableScrollViewCallbacks {

    private static final String TAG = "MainActivity";
    private static final int OFF_SCREEN_LIMIT = 5;
    private static final boolean IS_SWIPEABLE = false;

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

        // Setup toolbar and sliding tabs for initial fragment in viewpager
        setupFragmentToolbarAt(0);
        setupSlidingTabsAt(0);
        registerFragmentToolbarCallbacks(0);
    }

    /*
     * ItemClickCallback
     */
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

    void setupFragmentToolbarAt(int position) {
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

    void setupSlidingTabsAt(int position) {
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        ViewPager pager;
        switch(position) {
            case 0:
                slidingTabLayout.setVisibility(View.VISIBLE);
                pager = ((SlidingTabsFragment) mMainActivityAdapter.instantiateItem(mViewPager, position)).getViewPager();
                slidingTabLayout.setViewPager(pager);
                break;
            case 1:
                slidingTabLayout.setVisibility(View.VISIBLE);
                pager = ((SlidingTabsFragment) mMainActivityAdapter.instantiateItem(mViewPager, position)).getViewPager();
                slidingTabLayout.setViewPager(pager);
                break;
            case 2:
                slidingTabLayout.setVisibility(View.GONE);
                break;
            case 3:
                slidingTabLayout.setVisibility(View.VISIBLE);
                pager = ((SlidingTabsFragment) mMainActivityAdapter.instantiateItem(mViewPager, position)).getViewPager();
                slidingTabLayout.setViewPager(pager);
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
            Log.d(TAG, "yuhu is instance");
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
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
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
            }
        }
    }

    private Fragment getCurrentFragment() {
        return mMainActivityAdapter.getItemAt(mViewPager.getCurrentItem());
    }

    private void propagateToolbarState(boolean isShown) {
        int toolbarHeight = mToolbarView.getHeight();

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
                Fragment childFragment = slidingTabsFragment.getFragmentAt(i);
                if (childFragment == null) {
                    continue;
                }

                View view = childFragment.getView();
                if (view == null) {
                    continue;
                }
                //propagateToolbarState(isShown, childFragment, toolbarHeight);
                propagateToolbarState(isShown, view, toolbarHeight);
            }
        }
    }

    private void propagateToolbarState(boolean isShown, View view, int toolbarHeight) {
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        if (recyclerView == null) {
            return;
        }
        LayoutManager layoutManager = (LayoutManager) recyclerView.getLayoutManager();
        if (isShown) {
            // Scroll up
            if (layoutManager.findFirstVisibleItemPosition() == 1) {
                layoutManager.scrollToPosition(0);
            }
        } else {
            // Scroll down (to hide padding)
            if (layoutManager.findFirstVisibleItemPosition() == 0) {
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

    /*@Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            if (firstScroll) {
                float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
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

        ObservableRecyclerView recyclerView = getRecyclerView();

        if (scrollState == ScrollState.DOWN) {
            showToolbar();
            showMainTabs();
        } else if (scrollState == ScrollState.UP) {
            int toolbarHeight = mToolbarView.getHeight();
            int scrollY = recyclerView.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                hideToolbar();
                hideMainTabs();
            } else {
                showToolbar();
                showMainTabs();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (!toolbarIsShown() && !toolbarIsHidden()) {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
                showMainTabs();
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
            animateView(mHeaderView, 0, 200);
        }
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            animateView(mHeaderView, -toolbarHeight, 200);
        }
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

    private List<ObservableRecyclerView> getNonVisibleRecyclerViewsInFragment() {
        ArrayList<ObservableRecyclerView> viewArrayList = new ArrayList<>();

        BaseFragment baseFragment = getFragmentAt(mViewPager.getCurrentItem());
        if (baseFragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) baseFragment;
            for (int i = 0; i < slidingTabsFragment.getFragmentCount(); i++) {
                if (i != slidingTabsFragment.getViewPagerPosition()) {
                    RecyclerFragment recyclerFragment = slidingTabsFragment.getFragmentAt(i);
                    ObservableRecyclerView recyclerView = recyclerFragment.getRecyclerView();
                    viewArrayList.add(recyclerView);
                }
            }
        }
        return viewArrayList;
    }

    private ObservableRecyclerView getRecyclerView() {
        ObservableRecyclerView recyclerView = null;
        BaseFragment baseFragment = getFragmentAt(mViewPager.getCurrentItem());
        if (baseFragment instanceof SlidingTabsFragment) {
            RecyclerFragment currentFragment = ((SlidingTabsFragment) baseFragment).getCurrentFragment();
            recyclerView = currentFragment.getRecyclerView();
        }
        if (baseFragment instanceof RecyclerFragment) {
            recyclerView = ((RecyclerFragment) baseFragment).getRecyclerView();
        }
        return recyclerView;
    }*/
}