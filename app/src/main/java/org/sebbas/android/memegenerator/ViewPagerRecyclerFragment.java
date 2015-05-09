/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.mrengineer13.snackbar.SnackBar;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.tonicartos.superslim.LayoutManager;

import java.lang.ref.WeakReference;

public class ViewPagerRecyclerFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, DataLoaderCallback, SnackBar.OnMessageClickListener,
        RecyclerViewListener {

    private static final String TAG = "ViewPagerRecyclerFragment";

    static final int VIRAL = 0;
    static final int TIME = 1;
    static final int WINDOW_DAY = 2;
    static final int WINDOW_WEEK = 3;
    static final int WINDOW_MONTH = 4;
    static final int WINDOW_YEAR = 5;
    static final int WINDOW_ALL = 6;
    static final int QUERY = 7;
    static final int DEFAULTS = 8;
    static final int MY_MEMES = 9;
    static final int RECENT = 10;
    static final int FAVORITE_TEMPLATES = 11;
    static final int FAVORITE_INSTANCES = 12;
    static final int SEARCH = 13;
    static final int EXPLORE = 14;

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private SimpleRecyclerAdapter mSimpleRecyclerAdapter;
    private CircularProgressView mCircularProgressView;
    private ObservableRecyclerView mRecyclerView;
    private int mFragmentType;
    private int mLayoutMode;
    private boolean mIsRefreshable;
    private String mQuery;
    private WeakReference<MainActivity> mWeakReference;
    private RecyclerView.AdapterDataObserver mAdapterObserver;

    public static ViewPagerRecyclerFragment newInstance(int fragmentType, int layoutMode, boolean refreshable) {
        ViewPagerRecyclerFragment fragment = new ViewPagerRecyclerFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_type", fragmentType);
        args.putInt("layout_mode", layoutMode);
        args.putBoolean("refreshable", refreshable);
        fragment.setArguments(args);
        return fragment;
    }

    public static ViewPagerRecyclerFragment newInstance(int fragmentType, int layoutMode, boolean refreshable, String query) {
        ViewPagerRecyclerFragment fragment = new ViewPagerRecyclerFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_type", fragmentType);
        args.putInt("layout_mode", layoutMode);
        args.putBoolean("refreshable", refreshable);
        args.putString("query", query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Only get the query if it is present
        try {
            mQuery = getArguments().getString("query");
        } finally {
            mFragmentType = getArguments().getInt("fragment_type");
            mLayoutMode = getArguments().getInt("layout_mode");
            mIsRefreshable = getArguments().getBoolean("refreshable");
            mWeakReference = new WeakReference<>((MainActivity) getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);

        // Depending on arguments, enable or disable swipe refresh
        mSwipeRefreshLayout.setEnabled(mIsRefreshable);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        updatePlaceholder();
        setupSwipeRefreshLayout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        resetRecyclerView();
        resetSwipeRefreshLayout();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSimpleRecyclerAdapter.refreshData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, Utils.REFRESH_ICON_TIME_SHOWN);
    }

    @Override
    public void onDataLoadSuccessful() {
        mSimpleRecyclerAdapter.refreshUI();
    }

    private Fragment getCurrentFragmentFromViewPager() {
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.meme_pager);
        int position = viewPager.getCurrentItem();
        SlidingTabsFragmentAdapter adapter = (SlidingTabsFragmentAdapter) viewPager.getAdapter();

        ViewPagerRecyclerFragment viewPagerRecyclerFragment = (ViewPagerRecyclerFragment)
                adapter.getFragment(Integer.toString(position));

        return viewPagerRecyclerFragment;
    }

    @Override
    public void onConnectionUnavailable() {
        mSimpleRecyclerAdapter.refreshUI();

        // Checks when no nested fragments are present
        if (getParentFragment() == null) {
            if (getUserVisibleHint()) {
                showConnectionUnavailableNotification();
            }
        // Checks when nested fragments are present
        } else {
            if (getUserVisibleHint() && getCurrentFragmentFromViewPager() == this) {
                showConnectionUnavailableNotification();
            }
        }
    }

    @Override
    public void onConnectionTimeout() {
        mSimpleRecyclerAdapter.refreshUI();

        // Checks when no nested fragments are present
        if (getParentFragment() == null) {
            if (isVisible()) {
                showConnectionTimeoutNotification();
            }
        // Checks when nested fragments are present
        } else {
            if (isVisible() && getCurrentFragmentFromViewPager() == this) {
                showConnectionTimeoutNotification();
            }
        }
    }

    private void showConnectionUnavailableNotification() {
        new SnackBar.Builder(this.getActivity())
                .withOnClickListener(this)
                .withMessageId(R.string.connection_unavailable)
                .withActionMessageId(R.string.retry)
                .withDuration(Utils.NO_CONNECTION_HINT_TIME)
                .withBackgroundColorId(R.color.accent)
                .show();
    }

    private void showConnectionTimeoutNotification() {
        new SnackBar.Builder(this.getActivity())
                .withOnClickListener(this)
                .withMessageId(R.string.connection_timeout)
                .withActionMessageId(R.string.retry)
                .withDuration(Utils.TIMEOUT_HINT_TIME)
                .withBackgroundColorId(R.color.accent)
                .show();
    }

    @Override
    public void onMessageClick(Parcelable parcelable) {
        refreshAdapter();
    }

    private void updatePlaceholder() {
        if (adapterIsEmpty()) {
            mCircularProgressView.setVisibility(View.VISIBLE);
        } else {
            mCircularProgressView.setVisibility(View.GONE);
        }
    }

    private boolean adapterIsEmpty() {
        return (mSimpleRecyclerAdapter.getItemCount() == 0);
    }

    protected void setupRecyclerView() {
        mSimpleRecyclerAdapter = new SimpleRecyclerAdapter(this);

        // Register observer
        mAdapterObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updatePlaceholder();
            }
        };
        mSimpleRecyclerAdapter.registerAdapterDataObserver(mAdapterObserver);

        MainActivity parentActivity = (MainActivity) getActivity(); //mWeakReference.get();

        mRecyclerView.setAdapter(mSimpleRecyclerAdapter);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));

        int scrollDuration = getResources().getInteger(R.integer.scroll_duration);
        switch (mLayoutMode) {
            case UIOptions.GRID_LAYOUT:
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(UIOptions.getGridColumnCount(), StaggeredGridLayoutManager.VERTICAL));
                break;
            case UIOptions.LIST_LAYOUT:
                mRecyclerView.setLayoutManager(new LayoutManager(parentActivity));
                break;
            case UIOptions.CARD_LAYOUT:
            case UIOptions.SCROLLBOX_LAYOUT:
                mRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(
                        getActivity(),
                        LinearLayoutManager.VERTICAL,
                        false,
                        scrollDuration));
                break;
            default:
                mRecyclerView.setLayoutManager(new GridLayoutManager(parentActivity, UIOptions.getGridColumnCount()));
        }

        // TODO parentActivity does not implement ObservableScrollViewCallbacks
        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            mRecyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setSwipeableChildren(R.id.scroll);
    }

    private void recyclerViewMoveUp() {
        if (mRecyclerView.getChildCount() > 0) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    private void resetRecyclerView() {
        mSimpleRecyclerAdapter.unregisterAdapterDataObserver(mAdapterObserver);
        mRecyclerView.setScrollViewCallbacks(null);
    }

    private void resetSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(null);
    }

    public int getLayoutMode() {
        return mLayoutMode;
    }

    public int getFragmentType() {
        return mFragmentType;
    }

    public String getQuery() {
        return mQuery;
    }

    @Override
    public void filterAdapterWith(String s) {
        if (mSimpleRecyclerAdapter != null) {
            mSimpleRecyclerAdapter.getFilter().filter(s);
            recyclerViewMoveUp();
        }
    }

    @Override
    public void refreshAdapter() {
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();
        recyclerViewMoveUp();
    }
}
