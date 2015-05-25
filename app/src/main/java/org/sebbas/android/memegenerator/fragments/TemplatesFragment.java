package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.adapter.RecyclerFragmentAdapter;
import org.sebbas.android.memegenerator.adapter.SuperSlimRecyclerAdapter;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.UIOptions;

public class TemplatesFragment extends RecyclerFragment {

    private static final String TAG = "TemplatesFragment";

    private SuperSlimRecyclerAdapter mSuperSlimRecyclerAdapter;

    public static TemplatesFragment newInstance(int layoutMode, boolean isRefreshable) {
        TemplatesFragment fragment = new TemplatesFragment();
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

        View fillerView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header_small, null);
        mSuperSlimRecyclerAdapter = new SuperSlimRecyclerAdapter(this, fillerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        super.init(view);
        super.with(mSuperSlimRecyclerAdapter);

        return view;
    }
}
