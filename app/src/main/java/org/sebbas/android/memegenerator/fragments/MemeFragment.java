package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.MemeFragmentAdapter;

public class MemeFragment extends SlidingTabsFragment {

    public static final String TAG = "MemeFragment";
    public static final String TAG_CHILD_ONE = "MemeFragmentOne";
    public static final String TAG_CHILD_TWO = "MemeFragmentTwo";
    public static final String TAG_CHILD_THREE = "MemeFragmentThree";

    private static final boolean PAGER_SWIPEABLE = true;
    private static final int PAGER_OFF_SCREEN_LIMIT = 3;
    private static final int PAGER_START_POSITION = 0;
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
        super.init(view, PAGER_SWIPEABLE, PAGER_OFF_SCREEN_LIMIT, PAGER_START_POSITION);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
