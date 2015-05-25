package org.sebbas.android.memegenerator.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    private View mToolbarView;
    private FragmentManager mRetainedChildFragmentManager;

    private SlidingTabLayout mSlidingTabLayout;

    public void init(View view, SlidingTabsAdapter fragmentAdapter, boolean isSwipeable, int offScreenLimit) {

        mToolbarView = getActivity().findViewById(R.id.toolbar);
        mViewPager = (ToggleSwipeViewPager) view.findViewById(R.id.toogle_swipe_viewpager);
        mSlidingTabLayout = (SlidingTabLayout) getActivity().findViewById(R.id.sliding_tabs);

        // TODO move this to with()?
        mSlidingTabsAdapter = fragmentAdapter;
        mViewPager.setPagingEnabled(isSwipeable);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setOffscreenPageLimit(offScreenLimit);

        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    public void with(SlidingTabsAdapter fragmentAdapter, boolean isSwipeable, int offScreenLimit) {

    }

    public FragmentManager getRetainedChildFragmentManager() {
        if(mRetainedChildFragmentManager == null) {
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

    public RecyclerFragment getFragmentAt(int position) {
        return (RecyclerFragment) mSlidingTabsAdapter.instantiateItem(mViewPager, position);
    }

    public RecyclerFragment getCurrentFragment() {
        return getFragmentAt(mViewPager.getCurrentItem());
    }

    public int getFragmentCount() {
        return mViewPager.getChildCount();
    }

    public int getViewPagerPosition() {
        return mViewPager.getCurrentItem();
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