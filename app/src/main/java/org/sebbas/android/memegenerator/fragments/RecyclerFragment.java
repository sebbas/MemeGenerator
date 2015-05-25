package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.mrengineer13.snackbar.SnackBar;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.tonicartos.superslim.LayoutManager;

import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.DataLoaderCallback;
import org.sebbas.android.memegenerator.activities.MainActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.RecyclerViewListener;
import org.sebbas.android.memegenerator.ScrollingLinearLayoutManager;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.adapter.RecyclerFragmentAdapter;
import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.Utils;


public abstract class RecyclerFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, DataLoaderCallback, SnackBar.OnMessageClickListener,
        RecyclerViewListener, ToolbarCallback {

    private static final String TAG = "RecyclerFragment";

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerFragmentAdapter mRecyclerFragmentAdapter;
    private CircularProgressView mCircularProgressView;
    private ObservableRecyclerView mRecyclerView;
    private String mFragmentType;
    private int mLayoutMode;
    private boolean mIsRefreshable;
    private RecyclerView.AdapterDataObserver mAdapterObserver;
    private Toolbar mToolbarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentType = getArguments().getString("fragment_type");
        mLayoutMode = getArguments().getInt("layout_mode");
        mIsRefreshable = getArguments().getBoolean("refreshable");
    }

    public void init(View view) {
        mToolbarView = (Toolbar) getActivity().findViewById(R.id.toolbar);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.recycler_container);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);

        // Depending on arguments, enable or disable swipe refresh
        mSwipeRefreshLayout.setEnabled(mIsRefreshable);

        // Set custom progress icon position because of toolbar and sliding tabs layout
        int tabHeight = getResources().getDimensionPixelOffset(R.dimen.tab_height);
        int offset = getActionBarSize() + tabHeight;
        mSwipeRefreshLayout.setProgressViewOffset(true, offset,offset + tabHeight);

        // Bring activity ui elements to front
        getActivity().findViewById(R.id.header).bringToFront();
    }

    public void with(RecyclerFragmentAdapter recyclerFragmentAdapter) {
        mRecyclerFragmentAdapter = recyclerFragmentAdapter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.unregisterToolbarCallback();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        updatePlaceholder();
        setupSwipeRefreshLayout();
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();

        resetRecyclerView();
        resetSwipeRefreshLayout();
    }*/

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerFragmentAdapter.refreshData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, Utils.REFRESH_ICON_TIME_SHOWN);
    }

    @Override
    public void onDataLoadSuccessful() {
        mRecyclerFragmentAdapter.refreshUI();
    }

    @Override
    public void onConnectionUnavailable() {
        mRecyclerFragmentAdapter.refreshUI();

        // Checks when no nested fragments are present
        if (getParentFragment() == null) {
            if (getUserVisibleHint()) {
                showConnectionUnavailableNotification();
            }
        // Checks when nested fragments are present
        } else {
            if (getUserVisibleHint() /*&& getCurrentFragmentFromViewPager() == this*/) {
                showConnectionUnavailableNotification();
            }
        }
    }

    @Override
    public void onConnectionTimeout() {
        mRecyclerFragmentAdapter.refreshUI();

        // Checks when no nested fragments are present
        if (getParentFragment() == null) {
            if (isVisible()) {
                showConnectionTimeoutNotification();
            }
        // Checks when nested fragments are present
        } else {
            if (isVisible() /*&& getCurrentFragmentFromViewPager() == this*/) {
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
        return (mRecyclerFragmentAdapter.getItemCount() == 0);
    }

    private void setupRecyclerView() {

        // Register observer
        mAdapterObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updatePlaceholder();
            }
        };
        mRecyclerFragmentAdapter.registerAdapterDataObserver(mAdapterObserver);

        MainActivity parentActivity = (MainActivity) getActivity();

        mRecyclerView.setAdapter(mRecyclerFragmentAdapter);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.main_container));

        switch (mLayoutMode) {
            case UIOptions.GRID_LAYOUT:
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(UIOptions.getGridColumnCount(), StaggeredGridLayoutManager.VERTICAL));
                break;
            case UIOptions.SUPER_SLIM_LAYOUT:
                mRecyclerView.setLayoutManager(new LayoutManager(parentActivity));
                break;
            case UIOptions.LIST_LAYOUT:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
                break;
            default:
                mRecyclerView.setLayoutManager(new LayoutManager(parentActivity));
            /*case UIOptions.CARD_LAYOUT:
                int scrollDuration = getResources().getInteger(R.integer.scroll_duration);
                mRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(
                        getActivity(),
                        LinearLayoutManager.VERTICAL,
                        false,
                        scrollDuration));
                break;*/
        }
        mRecyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) getActivity());
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
        mRecyclerFragmentAdapter.unregisterAdapterDataObserver(mAdapterObserver);
        mRecyclerView.setScrollViewCallbacks(null);
    }

    private void resetSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(null);
    }

    public String getFragmentType() {
        return mFragmentType;
    }

    public ObservableRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void filterAdapterWith(String s) {
        if (mRecyclerFragmentAdapter != null) {
            mRecyclerFragmentAdapter.getFilter().filter(s);
            recyclerViewMoveUp();
        }
    }

    @Override
    public void refreshAdapter() {
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();
        recyclerViewMoveUp();
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
    public void onRefreshClicked() {
    }

    @Override
    public void onBackPressed() {
    }
}
