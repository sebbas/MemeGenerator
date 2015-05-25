package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.CardsRecyclerAdapter;

public class ImgurChildFragment extends RecyclerFragment {

    private static final String TAG = "SlidingTabsChildFragment";

    private CardsRecyclerAdapter mCardsRecyclerAdapter;

    public static ImgurChildFragment newInstance(int layoutMode, boolean isRefreshable) {
        ImgurChildFragment fragment = new ImgurChildFragment();
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

        View fillerView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header_big, null);
        mCardsRecyclerAdapter = new CardsRecyclerAdapter(this, fillerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        super.init(view);
        super.with(mCardsRecyclerAdapter);

        return view;
    }
}
