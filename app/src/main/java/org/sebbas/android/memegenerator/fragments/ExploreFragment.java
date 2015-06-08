package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.ScrollBoxRecyclerAdapter;

public class ExploreFragment extends RecyclerFragment {

    private static final String TAG = "ExploreFragment";

    private ScrollBoxRecyclerAdapter mScrollBoxRecyclerAdapter;

    public static ExploreFragment newInstance(int layoutMode, boolean isRefreshable) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString("fragment_type", TAG);
        args.putInt("layout_mode", layoutMode);
        args.putBoolean("refreshable", isRefreshable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScrollBoxRecyclerAdapter = new ScrollBoxRecyclerAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        super.init(view);
        super.with(mScrollBoxRecyclerAdapter);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
