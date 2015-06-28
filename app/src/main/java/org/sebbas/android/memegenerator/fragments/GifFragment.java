package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.adapter.GifFragmentAdapter;
import org.sebbas.android.memegenerator.R;

public class GifFragment extends SlidingTabsFragment {

    public static final String TAG = "GifFragment";
    private static final boolean PAGER_SWIPEABLE = true;
    private static final int PAGER_OFF_SCREEN_LIMIT = 3;
    private static final int PAGER_START_POSITION = 0;
    private static int[] TAB_TITLES = {
            R.string.all_items,
            R.string.popular_items,
            R.string.new_items};

    private GifFragmentAdapter mGifFragmentAdapter;

    public static GifFragment newInstance(int position) {
        GifFragment gifFragment = new GifFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_IN_PARENT, position);
        gifFragment.setArguments(args);
        return gifFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGifFragmentAdapter == null) {
            mGifFragmentAdapter = new GifFragmentAdapter(getActivity(), getChildFragmentManager(), TAB_TITLES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        View view = inflater.inflate(R.layout.fragment_slidingtabs, container, false);

        super.with(mGifFragmentAdapter);
        super.init(view, PAGER_SWIPEABLE, PAGER_OFF_SCREEN_LIMIT, PAGER_START_POSITION);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
