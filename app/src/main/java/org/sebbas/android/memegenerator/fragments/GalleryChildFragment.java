package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.CardsRecyclerAdapter;

public class GalleryChildFragment extends RecyclerFragment {

    public static final String TAG = "GalleryChildFragment";

    private CardsRecyclerAdapter mCardsRecyclerAdapter;

    public static GalleryChildFragment newInstance(int layoutMode, boolean isRefreshable, int position) {
        GalleryChildFragment fragment = new GalleryChildFragment();
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

        mCardsRecyclerAdapter = new CardsRecyclerAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        super.init(view);
        super.with(mCardsRecyclerAdapter);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
