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
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class ViewPagerGridViewFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, DataLoader.DataLoaderCallback {

    private static final int INITIAL_DELAY_MILLIS = 300;

    private String mUrl;
    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private GoogleCardsAdapter mGoogleCardsAdapter;
    private CircularProgressView mCircularProgressView;
    private ObservableGridView mGridView;
    private DataLoader mDataLoader;

    public static ViewPagerGridViewFragment newInstance(String url) {
        ViewPagerGridViewFragment fragment = new ViewPagerGridViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
        mDataLoader = new DataLoader(this, mUrl);
        mGoogleCardsAdapter = new GoogleCardsAdapter(this.getActivity(), mDataLoader);

        mDataLoader.loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);

        mGridView = (ObservableGridView) view.findViewById(R.id.scroll);
        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBarActivity parentActivity = (ActionBarActivity) getActivity();

        // Animation for gridview
        // SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mGoogleCardsAdapter);
        // swingBottomInAnimationAdapter.setAbsListView(mGridView);
        // assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        // swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        // mGridView.setAdapter(swingBottomInAnimationAdapter);

        mGridView.setAdapter(mGoogleCardsAdapter);
        mGridView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListCallback) getActivity()).onItemClick(position, mDataLoader);
            }
        });
        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            mGridView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setSwipeableChildren(R.id.scroll);

        // Fetch data
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                mDataLoader.loadData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 4000);
    }

    @Override
    public void onDataLoadComplete() {
        mCircularProgressView.setVisibility(View.GONE);
        mGoogleCardsAdapter.notifyDataSetChanged();
    }
}
