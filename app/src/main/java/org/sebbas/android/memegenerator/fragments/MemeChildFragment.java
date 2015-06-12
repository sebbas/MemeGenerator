package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.adapter.SuperSlimRecyclerAdapter;
import org.sebbas.android.memegenerator.dataloader.DataLoader;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

import java.util.List;

public class MemeChildFragment extends RecyclerFragment {

    public static final String TAG = "MemeChildFragment";

    public static MemeChildFragment newInstance(int layoutMode, boolean isRefreshable, int position) {
        MemeChildFragment fragment = new MemeChildFragment();
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
        String url = Utils.getUrlForData(0, this);
        super.load(url, DataLoader.INTERNET);

        // Setup adapter
        List<LineItem> lineItems = super.getLineItems();
        SuperSlimRecyclerAdapter superSlimRecyclerAdapter = new SuperSlimRecyclerAdapter(this, lineItems);

        // Create the view
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        super.with(superSlimRecyclerAdapter);
        super.init(view);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
