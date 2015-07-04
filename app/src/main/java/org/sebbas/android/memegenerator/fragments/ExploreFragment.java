package org.sebbas.android.memegenerator.fragments;

public class ExploreFragment extends RecyclerFragment {

    public static final String TAG = "ExploreFragment";

    public static RecyclerFragment newInstance(int position) {
        return RecyclerFragment.newInstance(
                RecyclerFragment.EXPLORE_FRAGMENT,
                RecyclerFragment.LIST_LAYOUT,
                RecyclerFragment.CARD,
                false, position);
    }
}
