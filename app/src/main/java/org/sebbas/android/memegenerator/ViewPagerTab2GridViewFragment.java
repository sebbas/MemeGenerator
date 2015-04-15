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

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class ViewPagerTab2GridViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, GoogleCardsAdapter.AdapterCallback {

    private static final int INITIAL_DELAY_MILLIS = 300;

    private String mUrl;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GoogleCardsAdapter mGoogleCardsAdapter;
    private CircularProgressView mCircularProgressView;

    public static ViewPagerTab2GridViewFragment newInstance(String url) {
        ViewPagerTab2GridViewFragment fragment = new ViewPagerTab2GridViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateAdapter() {
        //mGoogleCardsAdapter.triggerAsyncLoad();
        mGoogleCardsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
        mGoogleCardsAdapter = new GoogleCardsAdapter(this.getActivity(), this,  mUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);

        Activity parentActivity = getActivity();
        final ObservableGridView gridView = (ObservableGridView) view.findViewById(R.id.scroll);

        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mGoogleCardsAdapter);
        swingBottomInAnimationAdapter.setAbsListView(gridView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

        gridView.setAdapter(swingBottomInAnimationAdapter);
        gridView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            gridView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        // Setup pull to refresh listener
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);

        //mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);

        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                mGoogleCardsAdapter.triggerAsyncLoad();
                mGoogleCardsAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }

    @Override
    public void onDataLoadStarted() {
        //mCircularProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDataLoadFinished() {
        //mCircularProgressView.setVisibility(View.GONE);
    }
}
