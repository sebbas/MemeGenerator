package org.sebbas.android.memegenerator.fragments;

public class ChartFragment extends RecyclerFragment {

    public static final String TAG = "ChartFragment";

    public static RecyclerFragment newInstance(int position) {
        return RecyclerFragment.newInstance(
                ChartFragment.TAG,
                RecyclerFragment.LIST_LAYOUT,
                RecyclerFragment.CARD,
                false, position);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
