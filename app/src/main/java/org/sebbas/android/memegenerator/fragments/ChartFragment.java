package org.sebbas.android.memegenerator.fragments;

public class ChartFragment extends RecyclerFragment {

    public static final String TAG = "ChartFragment";

    public static RecyclerFragment newInstance(int position) {
        return RecyclerFragment.newInstance(
                RecyclerFragment.EXPLORE_FRAGMENT,
                RecyclerFragment.LIST_LAYOUT,
                RecyclerFragment.CARD,
                false, position);
    }
}
