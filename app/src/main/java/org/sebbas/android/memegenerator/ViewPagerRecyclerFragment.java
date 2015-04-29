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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.mrengineer13.snackbar.SnackBar;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.tonicartos.superslim.LayoutManager;


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

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private SimpleRecyclerAdapter mSimpleRecyclerAdapter;
    private CircularProgressView mCircularProgressView;
    private ObservableRecyclerView mRecyclerView;
    //private DataLoader mDataLoader;
    private int mFragmentType;
    private int mLayoutMode;
    private String mQuery;

    public static ViewPagerRecyclerFragment newInstance(int fragmentType, int layoutMode) {
        ViewPagerRecyclerFragment fragment = new ViewPagerRecyclerFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_type", fragmentType);
        args.putInt("layout_mode", layoutMode);
        fragment.setArguments(args);
        return fragment;
    }

    public static ViewPagerRecyclerFragment newInstance(int fragmentType, int layoutMode, String query) {
        ViewPagerRecyclerFragment fragment = new ViewPagerRecyclerFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_type", fragmentType);
        args.putInt("layout_mode", layoutMode);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        setHasOptionsMenu(true);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);

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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (mSimpleRecyclerAdapter != null) {
                    mSimpleRecyclerAdapter.getFilter().filter(s);
                    recyclerViewMoveUp();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                onRefresh();
        }
        return super.onOptionsItemSelected(item);
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
        int index = viewPager.getCurrentItem();
        SlidingTabsFragmentAdapter adapter = (SlidingTabsFragmentAdapter) viewPager.getAdapter();

        ViewPagerRecyclerFragment fragment = (ViewPagerRecyclerFragment)
                adapter.getFragment(Integer.toString(index));
        return fragment;
    }

    @Override
    public void onConnectionUnavailable() {
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
                .withDuration((short) 5000)
                .withBackgroundColorId(R.color.accent)
                .show();
    }

    private void showConnectionTimeoutNotification() {
        new SnackBar.Builder(this.getActivity())
                .withOnClickListener(this)
                .withMessageId(R.string.connection_timeout)
                .withActionMessageId(R.string.retry)
                .withDuration((short) 5000)
                .withBackgroundColorId(R.color.accent)
                .show();
    }

    @Override
    public void onMessageClick(Parcelable parcelable) {
    }

    private void updatePlaceholder() {
        if (adapterIsEmpty()) {
            mCircularProgressView.setVisibility(View.VISIBLE);
        } else {
            mCircularProgressView.setVisibility(View.GONE);
        }
    }

    private boolean adapterIsEmpty() {
        //mSimpleRecyclerAdapter.refreshData();
        return (mSimpleRecyclerAdapter.getItemCount() == 0);
    }

    protected void setupRecyclerView() {
        mSimpleRecyclerAdapter = new SimpleRecyclerAdapter(this);

        mSimpleRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updatePlaceholder();
            }
        });

        final MainActivity parentActivity = (MainActivity) getActivity();

        mRecyclerView.setAdapter(mSimpleRecyclerAdapter);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));
        switch (mLayoutMode) {
            case UIOptions.GRID_LAYOUT:
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(UIOptions.getGridColumnCount(), StaggeredGridLayoutManager.VERTICAL));
                break;
            case UIOptions.LIST_LAYOUT:
                mRecyclerView.setLayoutManager(new LayoutManager(parentActivity));
                break;
            case UIOptions.CARD_LAYOUT:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
                break;
            default:
                mRecyclerView.setLayoutManager(new GridLayoutManager(parentActivity, UIOptions.getGridColumnCount()));
        }

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
            mRecyclerView.scrollToPosition(0);
        }
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
        onRefresh();
        Toast.makeText(this.getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
    }
}
