package org.sebbas.android.memegenerator.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.clans.fab.FloatingActionButton;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.ToggleSwipeViewPager;
import org.sebbas.android.memegenerator.adapter.CardPagerAdapter;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

import java.util.ArrayList;
import java.util.Arrays;

public class EditorActivity extends BaseActivity implements FragmentCallback, ToolbarCallback {

    public static final String TAG = "EditorActivity";
    public static final String START_POSITION = "startPosition";
    public static final String LINE_ITEMS = "lineItems";
    private static final int OFF_SCREEN_LIMIT = 3;
    private static final boolean IS_SWIPEABLE = true;
    private static final boolean IS_SMOOTH_SCROLL = false;
    private static final int ANIMATION_SPEED = 300;

    private int mStartPosition;
    private String[] mTabTitles;
    private CardPagerAdapter mCardPagerAdapter;
    private ArrayList<LineItem> mLineItems;
    private View mHeaderView;
    private View mToolbarView;
    private ToggleSwipeViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private FloatingActionButton mFloatingActionButton;
    private boolean mEditModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Get line items and start position / current item in pager
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mStartPosition = extras.getInt(START_POSITION);
                mLineItems = extras.getParcelableArrayList(LINE_ITEMS);
            }
        } else {
            mStartPosition = savedInstanceState.getInt(START_POSITION);
            mLineItems = savedInstanceState.getParcelableArrayList(LINE_ITEMS);
        }

        // Get all item titles and plug them into array
        mTabTitles = new String[mLineItems.size()];
        for (int i = 0; i < mLineItems.size(); i++) {
            String itemTitle = mLineItems.get(i).getTitle();
            mTabTitles[i] = itemTitle;
        }

        // This makes the actionbar in multitask mode in Android Lollipop look a bit nicer
        setCustomLollipopActionBar();

        mHeaderView = findViewById(R.id.header);
        mToolbarView = findViewById(R.id.toolbar);
        mCardPagerAdapter = new CardPagerAdapter(this, getSupportFragmentManager(), mTabTitles);

        mViewPager = (ToggleSwipeViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setPagingEnabled(IS_SWIPEABLE);
        mViewPager.setSmoothScrollEnabled(IS_SMOOTH_SCROLL);
        mViewPager.setAdapter(mCardPagerAdapter);
        mViewPager.setOffscreenPageLimit(OFF_SCREEN_LIMIT);
        mViewPager.setCurrentItem(mStartPosition);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        // Setup top sliding tabs
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        mSlidingTabLayout.setDistributeEvenly(true);

        // Setup toolbar title
        super.setupToolbar(TAG);
        super.registerToolbarCallback(this);

        int TAB_TITLE_RANGE = 4;
        int fromPosition = (mStartPosition < TAB_TITLE_RANGE) ? 0 : mStartPosition - TAB_TITLE_RANGE;
        int toPosition = (mStartPosition > mTabTitles.length - TAB_TITLE_RANGE) ? mTabTitles.length : (mStartPosition + TAB_TITLE_RANGE);
        mSlidingTabLayout.setViewPager(mViewPager, fromPosition, toPosition);
        //Log.d(TAG, "fromPosition: " + fromPosition + " , toPosition: " + toPosition + " , startPosition: " + mStartPosition);

        // Setup floating action button
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditModeEnabled = !mEditModeEnabled;
                toggleFabIcon();
                toggleViewPagerSwipeable();
            }
        });
    }

    private void toggleFabIcon() {
        if (mEditModeEnabled) {
            mFloatingActionButton.setImageResource(R.drawable.ic_done_white_24dp);
        } else {
            mFloatingActionButton.setImageResource(R.drawable.ic_edit_white_24dp);
        }
    }

    private void toggleViewPagerSwipeable() {
        if (mEditModeEnabled) {
            mViewPager.setPagingEnabled(false);
        } else {
            mViewPager.setPagingEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(START_POSITION, mStartPosition);
        outState.putParcelableArrayList(LINE_ITEMS, mLineItems);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public LineItem getLineItemAt(int position) {
        return mLineItems.get(position);
    }

    @Override
    public void onFragmentComplete(BaseFragment baseFragment) {
    }

    @Override
    public void onItemClick(int itemPosition, ArrayList<LineItem> lineItems) {
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onToolbarBackPressed() {
        this.onBackPressed();
    }

    @Override
    public int getMainPagerPosition() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public int getLastFragmentPositionMain() {
        return 0;
    }

    private class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
