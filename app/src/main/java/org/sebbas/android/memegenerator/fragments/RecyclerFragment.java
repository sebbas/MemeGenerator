package org.sebbas.android.memegenerator.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.mrengineer13.snackbar.SnackBar;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.tonicartos.superslim.LayoutManager;

import org.sebbas.android.memegenerator.DividerItemDecoration;
import org.sebbas.android.memegenerator.FastScroller;
import org.sebbas.android.memegenerator.GridSpacingItemDecoration;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.adapter.RecyclerFragmentAdapter;
import org.sebbas.android.memegenerator.dataloader.DataLoader;
import org.sebbas.android.memegenerator.interfaces.DataLoaderCallback;
import org.sebbas.android.memegenerator.activities.MainActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.Utils;

import java.util.ArrayList;

public class RecyclerFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, DataLoaderCallback, SnackBar.OnMessageClickListener, ToolbarCallback {

    private static final String TAG = "RecyclerFragment";
    private static final String LINE_ITEMS = "lineItems";

    // Keys for values in bundle
    private static final String ARG_FRAGMENT_TYPE = "ARG_FRAGMENT_TYPE";
    private static final String ARG_LAYOUT_MODE = "ARG_LAYOUT_MODE";
    private static final String ARG_ITEM_TYPE = "ARG_ITEM_TYPE";
    private static final String ARG_IS_REFRESHABLE = "ARG_IS_REFRESHABLE";
    private static final String ARG_POSITION_IN_PARENT = "ARG_POSITION_IN_PARENT";

    // Layout mode
    public static final int GRID_LAYOUT = 0;
    public static final int LIST_LAYOUT = 1;

    // Item type
    public static final int CARD = 2;
    public static final int SUPER_SLIM = 3;
    public static final int EXPLORE = 4;

    // Specific setup for grid layout
    private static final int GRID_COLUMN_COUNT = 3;
    private static final int GRID_SPACING = 5;
    private static final boolean GRID_INCLUDE_EDGE = false;

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerFragmentAdapter mRecyclerFragmentAdapter;
    private CircularProgressView mCircularProgressView;
    private ObservableRecyclerView mRecyclerView;
    private String mFragmentTag;
    private int mLayoutMode;
    private int mItemType;
    private boolean mIsRefreshable;
    private RecyclerView.AdapterDataObserver mAdapterObserver;
    private DataLoader mDataLoader;
    private int mPositionInParent;
    private FastScroller mFastScroller;

    protected ArrayList<LineItem> mLineItems;

    public static RecyclerFragment newInstance(String tag, int layoutMode, int itemType, boolean isRefreshable, int position) {
        RecyclerFragment fragment = new RecyclerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TYPE, tag);
        args.putInt(ARG_LAYOUT_MODE, layoutMode);
        args.putInt(ARG_ITEM_TYPE, itemType);
        args.putBoolean(ARG_IS_REFRESHABLE, isRefreshable);
        args.putInt(ARG_POSITION_IN_PARENT, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFragmentTag = args.getString(ARG_FRAGMENT_TYPE);
            mLayoutMode = args.getInt(ARG_LAYOUT_MODE);
            mItemType = args.getInt(ARG_ITEM_TYPE);
            mIsRefreshable = args.getBoolean(ARG_IS_REFRESHABLE, false);
            mPositionInParent = args.getInt(ARG_POSITION_IN_PARENT, 0);
        }
        mDataLoader = new DataLoader(this);

        int location = Utils.getLoadingLocation(mFragmentTag);
        String url = Utils.getDataUrl(mFragmentTag);
        boolean isNetworkLoad = Utils.isNetworkLoad(getActivity(), mFragmentTag);
        mDataLoader.loadData(location, url, isNetworkLoad);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        init(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.saveDate(getActivity(), mFragmentTag);
    }

    private void init(View view) {

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.recycler_swipe_refresh);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        mFastScroller = (FastScroller) view.findViewById(R.id.fastscroller);

        // Depending on arguments, enable or disable swipe refresh
        mSwipeRefreshLayout.setEnabled(mIsRefreshable);

        // Set custom progress icon position because of toolbar and sliding tabs layout
        //int tabHeight = getResources().getDimensionPixelOffset(R.dimen.tab_height);
        //int offset = getActionBarSize() + tabHeight;
        //mSwipeRefreshLayout.setProgressViewOffset(true, offset,offset + tabHeight);

        // Bring activity ui elements to front
        ((MainActivity) getActivity()).bringMainNavigationToFront();

        setupRecyclerView();
        setupSwipeRefreshLayout();
        setupFastScroller();

        super.onFragmentComplete(this);
    }

    private void setupRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setTouchInterceptionViewGroup((ViewGroup) getActivity().findViewById(R.id.main_container));

        switch (mLayoutMode + "|" + mItemType) {
            case GRID_LAYOUT + "|" + CARD:
                final GridLayoutManager manager = new GridLayoutManager(getActivity(),
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
            case GRID_LAYOUT + "|" + SUPER_SLIM:
                mRecyclerView.setLayoutManager(new LayoutManager(getActivity()));
                break;
            case LIST_LAYOUT + "|" + CARD:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                break;
            case LIST_LAYOUT + "|" + SUPER_SLIM:
                mRecyclerView.setLayoutManager(new LayoutManager(getActivity()));
                break;
            case LIST_LAYOUT + "|" + EXPLORE:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            default:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mRecyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.unregisterToolbarCallback();
    }

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
    public void onDataLoadSuccess() {
        mDataLoader.loadLineItems(mItemType);
    }

    @Override
    public void onConnectionUnavailable() {

        // Checks when no nested fragments are present
        if (getParentFragment() == null) {
            if (isVisibleToUser()) {
                showConnectionUnavailableNotification();
            }
        // Checks when nested fragments are present
        } else {
            if (isVisibleToUser()) {
                showConnectionUnavailableNotification();
            }
        }
        mDataLoader.loadLineItems(mItemType);
    }

    @Override
    public void onDataLoadError() {

        // Checks when no nested fragments are present
        if (getParentFragment() == null) {
            if (isVisibleToUser()) {
                showConnectionTimeoutNotification();
            }
        // Checks when nested fragments are present
        } else {
            if (isVisibleToUser()) {
                showConnectionTimeoutNotification();
            }
        }
        mDataLoader.loadLineItems(mItemType);
    }

    @Override
    public void onLineItemsComplete() {
        mLineItems = mDataLoader.getLineItems();

        setLoading(false);
        setupAdapter();
    }


    @Override
    public boolean isVisibleToUser() {
        boolean isVisibleToUser = true;
        if (getActivity() != null) {
            if (getParentFragment() != null) {
                isVisibleToUser &= ((BaseFragment) getParentFragment()).isVisibleToUser();
            }
            isVisibleToUser &= ((BaseActivity) getActivity()).getMainPagerPosition() == mPositionInParent;
        }
        return isVisibleToUser;
    }


    /*@Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }*/

    @Override
    public void onFilterComplete() {
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

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            mCircularProgressView.setVisibility(View.VISIBLE);
        } else {
            mCircularProgressView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupAdapter() {
        if (mRecyclerFragmentAdapter == null) {
            mRecyclerFragmentAdapter = new RecyclerFragmentAdapter(getActivity(), mLineItems, mItemType);

            // Register observer
            mAdapterObserver = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    setLoading(false);
                }
            };
            mRecyclerFragmentAdapter.registerAdapterDataObserver(mAdapterObserver);
            mRecyclerView.setAdapter(mRecyclerFragmentAdapter);
        } else {
            ArrayList<LineItem> lineItems = mDataLoader.getLineItems();
            mRecyclerFragmentAdapter.setLineItems(lineItems);
            mRecyclerFragmentAdapter.notifyDataSetChanged();
        }

    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setSwipeableChildren(R.id.scroll);
    }

    private void setupFastScroller() {
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        mFastScroller.setRecyclerView(mRecyclerView);

        // Set listener to hide and show fast scroller on scroll
        mRecyclerView.addOnScrollListener(new FastScrollHelperListener());

        // Set custom scroll area because of top padding items
        mFastScroller.setStartScrollPosition(1);

        // Make sure that fast scroller is not visible when view is first created
        mFastScroller.setVisibility(View.GONE);
    }

    private void recyclerViewMoveUp() {
        if (mRecyclerView.getChildCount() > 0) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    private void filterDataWith(String s) {
        mDataLoader.filter(s);
    }

    /*public ArrayList<LineItem> getLineItems() {
        switch (mItemType) {
            case CARD:
                return mDataLoader.getLineItems();
            case SUPER_SLIM:
                return mDataLoader.getSuperSlimLineItems();
            case EXPLORE:
                return mDataLoader.getLineItems();
            default:
                return mDataLoader.getLineItems();
        }
    }*/

    private void updateLineItems() {
        mDataLoader.loadLineItems(mItemType);
    }

    private void refreshAdapter() {
        if (mLineItems != null) {
            mRecyclerFragmentAdapter.notifyDataSetChanged();
        }
    }

    public int getFirstVisibleItemPosition() {
        int position = 0;
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();

        if (layoutManager != null && mRecyclerView.getAdapter() != null) {
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
        this.recyclerViewMoveUp();

        return true;
    }

    @Override
    public void onToolbarBackPressed() {

    }

    @Override
    public String getFragmentTag() {
        return mFragmentTag;
    }

    private class FastScrollHelperListener extends RecyclerView.OnScrollListener {

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }

        // Show and hide fast scroll bar in x direction
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy < 0) {
                animateView(mFastScroller, 30, 100);
            } else if (dy > 0) {
                mFastScroller.setVisibility(View.VISIBLE);
                animateView(mFastScroller, 0, 100);
            }
        }
    }

    private void animateView(View view, int translationX, int duration) {
        ViewPropertyAnimator.animate(view).translationX(translationX).setDuration(duration).start();
    }
}
