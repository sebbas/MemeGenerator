package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.MemeFragmentAdapter;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public class MemeFragment extends SlidingTabsFragment {

    public static final String TAG = "MemeFragment";
    private static final int OFF_SCREEN_LIMIT = 3;
    private static int[] TAB_TITLES = {
            R.string.all_items,
            R.string.popular_items,
            R.string.new_items};

    private MemeFragmentAdapter mMemeFragmentAdapter;

    public static MemeFragment newInstance(int position) {
        MemeFragment memeFragment = new MemeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_IN_PARENT, position);
        memeFragment.setArguments(args);
        return memeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mMemeFragmentAdapter == null) {
            mMemeFragmentAdapter = new MemeFragmentAdapter(getActivity(), getChildFragmentManager(), TAB_TITLES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        View view = inflater.inflate(R.layout.fragment_slidingtabs, container, false);

        super.with(mMemeFragmentAdapter);
        super.init(view, true, OFF_SCREEN_LIMIT);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
