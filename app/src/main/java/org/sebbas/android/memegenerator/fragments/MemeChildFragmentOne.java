package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.adapter.SuperSlimRecyclerAdapter;
import org.sebbas.android.memegenerator.dataloader.DataLoader;

import java.util.ArrayList;

public class MemeChildFragmentOne extends RecyclerFragment {

    public static final String TAG = "MemeChildFragmentOne";

    private SuperSlimRecyclerAdapter mSuperSlimRecyclerAdapter;

    public static MemeChildFragmentOne newInstance(int layoutMode, boolean isRefreshable, int position) {
        MemeChildFragmentOne fragment = new MemeChildFragmentOne();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TYPE, TAG);
        args.putInt(ARG_LAYOUT_MODE, layoutMode);
        args.putBoolean(ARG_IS_REFRESHABLE, isRefreshable);
        args.putInt(ARG_POSITION_IN_PARENT, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get url for content
        String url = Utils.getUrlForData(TAG);
        super.load(url, DataLoader.INTERNET);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Setup adapter
        if (mSuperSlimRecyclerAdapter == null) {
            mSuperSlimRecyclerAdapter = new SuperSlimRecyclerAdapter(getActivity(), mLineItems);
        }

        // Create the view
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        super.with(mSuperSlimRecyclerAdapter);
        super.init(view);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
