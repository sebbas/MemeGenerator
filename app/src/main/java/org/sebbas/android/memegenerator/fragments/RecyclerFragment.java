package org.sebbas.android.memegenerator.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.swiperefreshmultipleviews.MultiSwipeRefreshLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.mrengineer13.snackbar.SnackBar;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.tonicartos.superslim.LayoutManager;

import org.sebbas.android.memegenerator.interfaces.DataLoaderCallback;
import org.sebbas.android.memegenerator.activities.MainActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.RecyclerViewListener;
import org.sebbas.android.memegenerator.ScrollingLinearLayoutManager;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.adapter.SimpleRecyclerAdapter;
import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.Utils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class RecyclerFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, DataLoaderCallback, SnackBar.OnMessageClickListener,
        RecyclerViewListener, ToolbarCallback {

    private static final String TAG = "RecyclerFragment";

    public static final int ALL = 0;
    public static final int MEMES = 1;
    public static final int GIFS = 2;
    public static final int QUERY = 3;
    public static final int DEFAULTS = 4;
    public static final int MY_MEMES = 5;
    public static final int RECENT = 6;
    public static final int FAVORITE_TEMPLATES = 7;
    public static final int FAVORITE_INSTANCES = 8;
    public static final int SEARCH = 9;
    public static final int EXPLORE = 10;

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private SimpleRecyclerAdapter mSimpleRecyclerAdapter;
    private CircularProgressView mCircularProgressView;
    private ObservableRecyclerView mRecyclerView;
    private int mFragmentType;
    private int mLayoutMode;
    private boolean mIsRefreshable;
    private String mQuery;
    private RecyclerView.AdapterDataObserver mAdapterObserver;
    private FragmentManager mRetainedChildFragmentManager;

    public static RecyclerFragment newInstance(int fragmentType, int layoutMode, boolean refreshable) {
        RecyclerFragment fragment = new RecyclerFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_type", fragmentType);
        args.putInt("layout_mode", layoutMode);
        args.putBoolean("refreshable", refreshable);
        fragment.setArguments(args);
        return fragment;
    }

    public static RecyclerFragment newInstance(int fragmentType, int layoutMode, boolean refreshable, String query) {
        RecyclerFragment fragment = new RecyclerFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.recycler_container);
        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        mRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);

        // Depending on arguments, enable or disable swipe refresh
        mSwipeRefreshLayout.setEnabled(mIsRefreshable);

        return view;
    }

    /*@Override
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
        } else {
            mRetainedChildFragmentManager = getChildFragmentManager();
        }
    }*/

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

    /*private Fragment getCurrentFragmentFromViewPager() {
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.meme_pager);
        int position = viewPager.getCurrentItem();
        SlidingTabsFragmentAdapter adapter = (SlidingTabsFragmentAdapter) viewPager.getAdapter();

        RecyclerFragment recyclerFragment = (RecyclerFragment)
                adapter.getFragment(Integer.toString(position));

        return recyclerFragment;
    }*/

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
            if (getUserVisibleHint() /*&& getCurrentFragmentFromViewPager() == this*/) {
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

        MainActivity parentActivity = (MainActivity) getActivity();

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
        SlidingTabsFragment parentFragment = (SlidingTabsFragment) getParentFragment();
        if (parentFragment instanceof ObservableScrollViewCallbacks) {
            mRecyclerView.setScrollViewCallbacks(parentFragment);
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
