package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.adapter.ImgurFragmentAdapter;
import org.sebbas.android.memegenerator.R;

public class ImgurFragment extends SlidingTabsFragment implements ToolbarCallback {

    private static final int OFF_SCREEN_LIMIT = 3;

    private static int[] TAB_TITLES = {
            R.string.all_images,
            R.string.memes,
            R.string.gifs};

    private ImgurFragmentAdapter mImgurFragmentAdapter;

    public static ImgurFragment newInstance() {
        return new ImgurFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = super.getRetainedChildFragmentManager();
        mImgurFragmentAdapter = new ImgurFragmentAdapter(getActivity(), fragmentManager, TAB_TITLES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        View view = inflater.inflate(R.layout.fragment_slidingtabs, container, false);

        super.init(view, mImgurFragmentAdapter, true, OFF_SCREEN_LIMIT);

        return view;
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
