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
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.HashMap;
import java.util.Map;

public class SlidingTabsFragment extends BaseFragment implements ObservableScrollViewCallbacks {

    private View mToolbarView;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private ViewPager mViewPager;
    private CacheFragmentStatePagerAdapter mPagerAdapter;
    private int mSlop;
    private boolean mScrolled;
    private ScrollState mLastScrollState;
    private int mTitleResource;
    private String[] mTitles;

    public static SlidingTabsFragment newInstance(int titleResource, String[] titles) {
        SlidingTabsFragment slidingTabsFragment = new SlidingTabsFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_title", titleResource);
        args.putStringArray("titles", titles);
        slidingTabsFragment.setArguments(args);
        return slidingTabsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleResource =  getArguments().getInt("fragment_title");
        mTitles = getArguments().getStringArray("titles");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_slidingtabs, container, false);

        // Setup the toolbar
        ActionBarActivity parentActivity = (ActionBarActivity) getActivity();
        ViewCompat.setElevation(view.findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        parentActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        mToolbarView = view.findViewById(R.id.toolbar);

        // Set custom fragment title
        setActionBarTitle(mTitleResource);

        // Choose adapter type depending on settings
        mPagerAdapter = new SlidingTabsFragmentAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.meme_pager);
        mViewPager.setAdapter(mPagerAdapter);

        // Always preload all pages in viewpager
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());

        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange.
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        view.findViewById(R.id.pager_wrapper).setPadding(0, getActionBarSize() + tabHeight, 0, tabHeight);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.main_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mViewPager);

        ViewConfiguration vc = ViewConfiguration.get(parentActivity);
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
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else if (!toolbarIsShown() && !toolbarIsHidden()) {
            // Toolbar is moving but doesn't know which to move:
            // you can change this to hideToolbar()
            showToolbar();
            //hideToolbar();
        }
    }

    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mViewPager.getCurrentItem());
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

    public class SlidingTabsFragmentAdapter extends CacheFragmentStatePagerAdapter {

        private Map<Integer, Fragment> mPageReferenceMap;

        public SlidingTabsFragmentAdapter(FragmentManager fm) {
            super(fm);
            mPageReferenceMap = new HashMap<>();
        }

        @Override
        protected Fragment createItem(int position) {

            switch (mTitleResource) {

                case R.string.instances:
                    return getInstancesPartFragment(position);
                case R.string.gallery:
                    return getGalleryPartFragment(position);
                default:
                    return getInstancesPartFragment(position);
            }
        }

        private Fragment getInstancesPartFragment(int position) {
            int id;
            int layout;
            BaseFragment f;

            switch (position) {
                case 0:
                    id = ViewPagerRecyclerViewFragment.VIRAL;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(0, f);
                    break;
                case 1:
                    id = ViewPagerRecyclerViewFragment.TIME;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(1, f);
                    break;
                case 2:
                    id = ViewPagerRecyclerViewFragment.WINDOW_DAY;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(2, f);
                    break;
                case 3:
                    id = ViewPagerRecyclerViewFragment.WINDOW_WEEK;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(3, f);
                    break;
                case 4:
                    id = ViewPagerRecyclerViewFragment.WINDOW_MONTH;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(4, f);
                    break;
                case 5:
                    id = ViewPagerRecyclerViewFragment.WINDOW_YEAR;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(5, f);
                    break;
                default:
                    id = ViewPagerRecyclerViewFragment.VIRAL;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(10, f);
                    break;
            }
            return f;
        }

        private Fragment getGalleryPartFragment(int position) {
            int id;
            int layout;
            BaseFragment f;

            switch (position) {
                case 0:
                    id = ViewPagerRecyclerViewFragment.MY_MEMES;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(6, f);
                    break;
                case 1:
                    id = ViewPagerRecyclerViewFragment.RECENT;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(7, f);
                    break;
                case 2:
                    id = ViewPagerRecyclerViewFragment.FAVORITE_TEMPLATES;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(8, f);
                    break;
                case 3:
                    id = ViewPagerRecyclerViewFragment.FAVORITE_INSTANCES;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(9, f);
                    break;
                default:
                    id = ViewPagerRecyclerViewFragment.VIRAL;
                    layout = UIOptions.getLayoutMode(id);
                    f = ViewPagerRecyclerViewFragment.newInstance(id, layout);
                    mPageReferenceMap.put(10, f);
                    break;
            }
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        public Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}