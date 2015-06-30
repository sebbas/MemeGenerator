package org.sebbas.android.memegenerator.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.sebbas.android.memegenerator.ToggleSwipeViewPager;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.SlidingTabsAdapter;


public abstract class SlidingTabsFragment extends BaseFragment {

    private static final String TAG = "SlidingTabsFragment";
    protected static final String ARG_POSITION_IN_PARENT = "ARG_POSITION_IN_PARENT";

    private SlidingTabsAdapter mSlidingTabsAdapter;
    private ToggleSwipeViewPager mViewPager;
    private int mLastPage;
    private int mPositionInParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPositionInParent = args.getInt(ARG_POSITION_IN_PARENT, 0);
        }
    }

    public void init(View view, boolean isSwipeable, int offScreenLimit, int startPosition) {

        if (mViewPager == null) {
            mViewPager = (ToggleSwipeViewPager) view.findViewById(R.id.tabs_viewpager);
            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    mLastPage = position;
                }
            });

            mViewPager.setPagingEnabled(isSwipeable);
            mViewPager.setOffscreenPageLimit(offScreenLimit);
            mViewPager.setAdapter(mSlidingTabsAdapter);
            mViewPager.setCurrentItem(startPosition);

            // Padding for tabs (only in portrait mode)
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
                view.findViewById(R.id.wrapper).setPadding(0, tabHeight, 0, 0);
            }
        }
        super.onFragmentComplete(this);
    }

    public void with(SlidingTabsAdapter fragmentAdapter) {
        mSlidingTabsAdapter = fragmentAdapter;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.unregisterToolbarCallback();
    }

    public int getChildFragmentCount() {
        return mViewPager.getChildCount();
    }

    public Fragment getFragmentAt(int position) {
        return  mSlidingTabsAdapter.getRegisteredFragment(position);
    }

    public int getCurrentPosition() {
        return mViewPager.getCurrentItem();
    }

    public Fragment getCurrentFragment() {
        return mSlidingTabsAdapter.getRegisteredFragment(getCurrentPosition());
    }

    public int getLastPage() {
        return mLastPage;
    }

    public int getPositionInParent() {
        return mPositionInParent;
    }
}