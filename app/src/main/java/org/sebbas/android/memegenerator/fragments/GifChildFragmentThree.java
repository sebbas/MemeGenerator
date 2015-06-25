package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.adapter.SimpleRecyclerAdapter;
import org.sebbas.android.memegenerator.dataloader.DataLoader;

import java.util.ArrayList;

public class GifChildFragmentThree extends RecyclerFragment {

    public static final String TAG = "GifChildFragmentThree";

    public static GifChildFragmentThree newInstance(int layoutMode, boolean isRefreshable, int position) {
        GifChildFragmentThree fragment = new GifChildFragmentThree();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TYPE, TAG);
        args.putInt(ARG_LAYOUT_MODE, layoutMode);
        args.putBoolean(ARG_IS_REFRESHABLE, isRefreshable);
        args.putInt(ARG_POSITION_IN_PARENT, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Get url for content
        String url = Utils.getUrlForData(TAG);
        super.load(url, DataLoader.INTERNET);

        // Setup adapter
        ArrayList<LineItem> lineItems = super.getLineItems();
        SimpleRecyclerAdapter simpleRecyclerAdapter = new SimpleRecyclerAdapter(getActivity(), lineItems);

        // Create the view
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        super.with(simpleRecyclerAdapter);
        super.init(view);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
