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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

public class ViewPagerRecyclerViewFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, DataLoader.DataLoaderCallback {

    static final int TEMPLATE_TRENDING_TYPE = 0;
    static final int TEMPLATE_POPULAR_TYPE = 1;
    static final int TEMPLATE_NEW_TYPE = 2;
    static final int TEMPLATE_RANDOM_TYPE = 3;
    static final int TEMPLATE_QUERY_TYPE = 4;
    static final int INSTANCE_POPULAR_TYPE = 5;
    static final int INSTANCE_NEW_TYPE = 6;

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private SimpleRecyclerAdapter mSimpleRecyclerAdapter;
    private CircularProgressView mCircularProgressView;
    private ObservableRecyclerView mRecyclerView;
    private DataLoader mDataLoader;
    private int mFragmentType;
    private int mLayoutMode;
    private int mPageIndex;
    private String mQuery;

    public static ViewPagerRecyclerViewFragment newInstance(int fragmentType, int layoutMode) {
        ViewPagerRecyclerViewFragment fragment = new ViewPagerRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_type", fragmentType);
        args.putInt("layout_mode", layoutMode);
        fragment.setArguments(args);
        return fragment;
    }

    public static ViewPagerRecyclerViewFragment newInstance(int fragmentType, int layoutMode, String query) {
        ViewPagerRecyclerViewFragment fragment = new ViewPagerRecyclerViewFragment();
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
            mPageIndex = 0;
            mDataLoader = new DataLoader(this);
            mDataLoader.loadData(getCurrentPageDataUrl());
            mSimpleRecyclerAdapter = new SimpleRecyclerAdapter(this, mDataLoader);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity parentActivity = getActivity();

        mRecyclerView.setAdapter(mSimpleRecyclerAdapter);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));
        switch (mLayoutMode) {
            case MemeFragment.GRID_LAYOUT:
                mRecyclerView.setLayoutManager(new GridLayoutManager(parentActivity, 3));
                break;
            case MemeFragment.LIST_LAYOUT:
                mRecyclerView.setLayoutManager(new GridLayoutManager(parentActivity, 3));
                break;
            case MemeFragment.CARD_LAYOUT:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
                break;
            default:
                mRecyclerView.setLayoutManager(new GridLayoutManager(parentActivity, 3));
        }

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            mRecyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //mDataLoader.loadData(getNextPageDataUrl());
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setSwipeableChildren(R.id.scroll);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                mDataLoader.loadData(getCurrentPageDataUrl());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onDataLoadComplete() {
        mCircularProgressView.setVisibility(View.GONE);
        mSimpleRecyclerAdapter.notifyDataSetChanged();
    }

    private String getCurrentPageDataUrl() {
        if (mFragmentType == ViewPagerRecyclerViewFragment.TEMPLATE_QUERY_TYPE) {
            return Data.getUrlForQuery(mPageIndex, mQuery);
        }
        return Data.getUrlForData(mPageIndex, mFragmentType);
    }

    private String getNextPageDataUrl() {
        mPageIndex = mPageIndex++;
        if (mFragmentType == ViewPagerRecyclerViewFragment.TEMPLATE_QUERY_TYPE) {
            return Data.getUrlForQuery(mPageIndex, mQuery);
        }
        return Data.getUrlForData(mPageIndex, mFragmentType);
    }

    public int getFragmentType() {
        return mFragmentType;
    }
}
