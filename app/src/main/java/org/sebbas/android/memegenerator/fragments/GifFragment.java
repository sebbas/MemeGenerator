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

    private static final String TAG = "GifFragment";
    private static final int OFF_SCREEN_LIMIT = 3;
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

        FragmentManager fragmentManager = super.getRetainedChildFragmentManager();
        if (mGifFragmentAdapter == null) {
            mGifFragmentAdapter = new GifFragmentAdapter(getActivity(), fragmentManager, TAB_TITLES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        View view = inflater.inflate(R.layout.fragment_slidingtabs, container, false);

        super.with(mGifFragmentAdapter);
        super.init(view, true, OFF_SCREEN_LIMIT);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
