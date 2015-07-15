package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.ExploreFragmentAdapter;

public class ExploreFragment extends SlidingTabsFragment {

    public static final String TAG = "ExploreFragment";
    public static final String TAG_CHILD_ONE = "ExploreFragmentOne";
    public static final String TAG_CHILD_TWO = "ExploreFragmentTwo";

    private static final boolean PAGER_SWIPEABLE = true;
    private static final int PAGER_OFF_SCREEN_LIMIT = 2;
    private static final int PAGER_START_POSITION = 0;
    private static int[] TAB_TITLES = {
            R.string.following,
            R.string.topics};

    private ExploreFragmentAdapter mExploreFragmentAdapter;

    public static ExploreFragment newInstance(int position) {
        ExploreFragment exploreFragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_IN_PARENT, position);
        exploreFragment.setArguments(args);
        return exploreFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mExploreFragmentAdapter == null) {
            mExploreFragmentAdapter = new ExploreFragmentAdapter(getActivity(), getChildFragmentManager(), TAB_TITLES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        super.onCreateView(inflater, container, bundle);
        View view = inflater.inflate(R.layout.fragment_slidingtabs, container, false);

        super.with(mExploreFragmentAdapter);
        super.init(view, PAGER_SWIPEABLE, PAGER_OFF_SCREEN_LIMIT, PAGER_START_POSITION);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
