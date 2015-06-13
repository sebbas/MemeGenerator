package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.mrengineer13.snackbar.SnackBar;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.tonicartos.superslim.LayoutManager;

import org.sebbas.android.memegenerator.GridSpacingItemDecoration;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.ScrollBarSectionIndicator;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.dataloader.DataLoader;
import org.sebbas.android.memegenerator.interfaces.DataLoaderCallback;
import org.sebbas.android.memegenerator.activities.MainActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.adapter.RecyclerFragmentAdapter;
import org.sebbas.android.memegenerator.Utils;

import java.util.List;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


public abstract class RecyclerFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, DataLoaderCallback, SnackBar.OnMessageClickListener, ToolbarCallback {

    private static final String TAG = "RecyclerFragment";

    // Keys for values in bundle
    protected static final String ARG_FRAGMENT_TYPE = "ARG_FRAGMENT_TYPE";
    protected static final String ARG_LAYOUT_MODE = "ARG_LAYOUT_MODE";
    protected static final String ARG_IS_REFRESHABLE = "ARG_IS_REFRESHABLE";
    protected static final String ARG_POSITION_IN_PARENT = "ARG_POSITION_IN_PARENT";

    // Layout options
    public static final int GRID_LAYOUT = 0;
    public static final int LIST_LAYOUT = 1;
    public static final int SUPER_SLIM_LAYOUT = 2;

    // Specific setup for grid layout
    private static final int GRID_COLUMN_COUNT = 3;
    private static final int GRID_SPACING = 5;
    private static final boolean GRID_INCLUDE_EDGE = true;

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerFragmentAdapter mRecyclerFragmentAdapter;
    private CircularProgressView mCircularProgressView;
    private ObservableRecyclerView mRecyclerView;
    private String mFragmentType;
    private int mLayoutMode;
    private boolean mIsRefreshable;
    private RecyclerView.AdapterDataObserver mAdapterObserver;
    private DataLoader mDataLoader;
    //private Parcelable mRecyclerState;
    private int mPositionInParent;
    private boolean mIsVisibleToUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFragmentType = args.getString(ARG_FRAGMENT_TYPE);
            mLayoutMode = args.getInt(ARG_LAYOUT_MODE);
            mIsRefreshable = args.getBoolean(ARG_IS_REFRESHABLE, false);
            mPositionInParent = args.getInt(ARG_POSITION_IN_PARENT, 0);
        }
        mDataLoader = new DataLoader(this);
    }

    public void init(View view) {

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.recycler_container);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);

        // Depending on arguments, enable or disable swipe refresh
        mSwipeRefreshLayout.setEnabled(mIsRefreshable);

        // Set custom progress icon position because of toolbar and sliding tabs layout
        //int tabHeight = getResources().getDimensionPixelOffset(R.dimen.tab_height);
        //int offset = getActionBarSize() + tabHeight;
        //mSwipeRefreshLayout.setProgressViewOffset(true, offset,offset + tabHeight);

        // Bring activity ui elements to front
        getActivity().findViewById(R.id.header).bringToFront();

        setupRecyclerView();
        updatePlaceholder();
        setupSwipeRefreshLayout();
        //restoreRecylerViewState();
        setupFastScroller(view);

        super.onFragmentComplete(this);
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
    public void onPause() {
        super.onPause();
        //saveRecyclerViewState();
    }

    /*private void restoreRecylerViewState() {
        if (mRecyclerState != null) {
            mRecyclerView.onRestoreInstanceState(mRecyclerState);
        }
    }

    private void saveRecyclerViewState() {
        if (mRecyclerState != null) {
            mRecyclerView.onSaveInstanceState();
        }
    }*/

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //mRecyclerFragmentAdapter.refreshData();
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
        //mRecyclerFragmentAdapter.refreshUI();

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
        //mRecyclerFragmentAdapter.refreshUI();

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

    @Override
    public void onFilterComplete() {
        Log.d(TAG, "onFilterComplete");
        this.updateLineItems();
        this.refreshAdapter();
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
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.main_container));

        switch (mLayoutMode) {
            case GRID_LAYOUT:
                final GridLayoutManager manager = new GridLayoutManager(parentActivity,
                        GRID_COLUMN_COUNT, GridLayoutManager.VERTICAL, false);
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (position == 0) ? manager.getSpanCount() : 1;
                    }
                });
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(GRID_COLUMN_COUNT, GRID_SPACING, GRID_INCLUDE_EDGE));
                break;
            case SUPER_SLIM_LAYOUT:
                mRecyclerView.setLayoutManager(new LayoutManager(parentActivity));
                break;
            case LIST_LAYOUT:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
                break;
            default:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
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

    private void setupFastScroller(View view) {
        // Grab your RecyclerView and the RecyclerViewFastScroller from the layout
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(mRecyclerView);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        mRecyclerView.setOnScrollListener(fastScroller.getOnScrollListener());

        // Connect the section indicator to the scroller
        ScrollBarSectionIndicator sectionTitleIndicator = (ScrollBarSectionIndicator) view.findViewById(R.id.fast_scroller_section_title_indicator);
        fastScroller.setSectionIndicator(sectionTitleIndicator);
    }

    private void recyclerViewMoveUp() {
        if (mRecyclerView.getChildCount() > 0) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    public String getFragmentType() {
        return mFragmentType;
    }

    public void filterDataWith(String s) {
        mDataLoader.filter(s);
    }

    public void load(String url, int location) {
        mDataLoader.load(url, location);
    }

    public List<LineItem> getLineItems() {
        switch (mLayoutMode) {
            case GRID_LAYOUT:
            case LIST_LAYOUT:
                return mDataLoader.getLineItems();
            case SUPER_SLIM_LAYOUT:
                return mDataLoader.getSuperSlimLineItems();
            default:
                return mDataLoader.getLineItems();
        }
    }

    private void updateLineItems() {
        List<LineItem> lineItems;
        switch (mLayoutMode) {
            case GRID_LAYOUT:
            case LIST_LAYOUT:
                lineItems = mDataLoader.getLineItems();
                break;
            case SUPER_SLIM_LAYOUT:
                lineItems = mDataLoader.getSuperSlimLineItems();
                break;
            default:
                lineItems = mDataLoader.getLineItems();
        }
        mRecyclerFragmentAdapter.setLineItems(lineItems);
    }

    private void refreshAdapter() {
        mRecyclerFragmentAdapter.notifyDataSetChanged();
    }

    public int getFirstVisibleItemPosition() {
        int position = 0;
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();

        if (layoutManager != null) {
            if (layoutManager instanceof LayoutManager) {
                position = ((LayoutManager) layoutManager).findFirstVisibleItemPosition();
            } else if (layoutManager instanceof LinearLayoutManager) {
                position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            }
        }
        return position;
    }

    public int getPositionInParent() {
        return mPositionInParent;
    }

    public int getParentPosition() {
        int position = -1;
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof SlidingTabsFragment) {
            SlidingTabsFragment slidingTabsFragment = (SlidingTabsFragment) parentFragment;
            position = slidingTabsFragment.getPositionInParent();
        }
        return position;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        this.filterDataWith(s);
        //this.recyclerViewMoveUp();

        return true;
    }

    @Override
    public void onBackPressed() {
    }

    public boolean isIsVisibleToUser() {
        return mIsVisibleToUser;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
    }
}
