package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.adapter.GalleryFragmentAdapter;
import org.sebbas.android.memegenerator.R;

public class GalleryFragment extends SlidingTabsFragment {

    public static final String TAG = "GalleryFragment";
    private static final int OFF_SCREEN_LIMIT = 3;
    private static int[] TAB_TITLES = {
            R.string.my_memes,
            R.string.favorites,
            R.string.recent};

    private GalleryFragmentAdapter mGalleryFragmentAdapter;

    public static GalleryFragment newInstance(int position) {
        GalleryFragment galleryFragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_IN_PARENT, position);
        galleryFragment.setArguments(args);
        return galleryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGalleryFragmentAdapter == null) {
            mGalleryFragmentAdapter = new GalleryFragmentAdapter(getActivity(), getChildFragmentManager(), TAB_TITLES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        View view = inflater.inflate(R.layout.fragment_slidingtabs, container, false);

        super.with(mGalleryFragmentAdapter);
        super.init(view, true, OFF_SCREEN_LIMIT);

        return view;
    }
    
    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
