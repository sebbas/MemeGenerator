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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class ViewPagerListViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, GoogleCardsAdapter.AdapterCallback {

    private static final int INITIAL_DELAY_MILLIS = 300;

    private String mUrl;
    private GoogleCardsAdapter mGoogleCardsAdapter;
    private ObservableListView mListView;
    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private CircularProgressView mCircularProgressView;

    public static ViewPagerListViewFragment newInstance(String url) {
        ViewPagerListViewFragment fragment = new ViewPagerListViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
        mGoogleCardsAdapter = new GoogleCardsAdapter(this.getActivity(), this,  mUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        mListView = (ObservableListView) view.findViewById(R.id.scroll);
        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity parentActivity = getActivity();

        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mGoogleCardsAdapter);
        swingBottomInAnimationAdapter.setAbsListView(mListView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

        mListView.setAdapter(swingBottomInAnimationAdapter);
        mListView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            mListView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setSwipeableChildren(R.id.scroll);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                mGoogleCardsAdapter.triggerAsyncLoad();
                mGoogleCardsAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 4000);
    }

    @Override
    public void onDataLoadFinished() {
        mCircularProgressView.setVisibility(View.GONE);
    }
}
