package org.sebbas.android.memegenerator.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;

import org.sebbas.android.memegenerator.ToggleSwipeViewPager;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.SlidingTabsAdapter;

import java.lang.reflect.Field;


public abstract class SlidingTabsFragment extends BaseFragment {

    private static final String TAG = "SlidingTabsFragment";

    private SlidingTabsAdapter mSlidingTabsAdapter;
    private ToggleSwipeViewPager mViewPager;
    private FragmentManager mRetainedChildFragmentManager;

    public void init(View view, SlidingTabsAdapter fragmentAdapter, boolean isSwipeable, int offScreenLimit) {

        mViewPager = (ToggleSwipeViewPager) view.findViewById(R.id.toogle_swipe_viewpager);

        // TODO move this to with()?
        mSlidingTabsAdapter = fragmentAdapter;
        mViewPager.setPagingEnabled(isSwipeable);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setOffscreenPageLimit(offScreenLimit);

        // Padding for tabs (only in portrait mode)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
            getActivity().findViewById(R.id.main_container).setPadding(0, tabHeight, 0, 0);
        }
    }

    public void with(SlidingTabsAdapter fragmentAdapter, boolean isSwipeable, int offScreenLimit) {

    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public FragmentManager getRetainedChildFragmentManager() {
        if (mRetainedChildFragmentManager == null) {
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

    public int getChildFragmentCount() {
        return mViewPager.getChildCount();
    }

    public RecyclerFragment getFragmentAt(int position) {
        return (RecyclerFragment) mSlidingTabsAdapter.instantiateItem(mViewPager, position);
    }

    public SlidingTabsAdapter getSlidingTabsAdapter() {
        return mSlidingTabsAdapter;
    }

    public int getCurrentPosition() {
        return mViewPager.getCurrentItem();
    }

    public Fragment getCurrentFragment() {
        return getFragmentAt(getCurrentPosition());
    }
}