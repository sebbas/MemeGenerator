package org.sebbas.android.memegenerator.fragments;

public class ExploreFragment extends RecyclerFragment {

    public static final String TAG = "ExploreFragment";

    public static RecyclerFragment newInstance(int position) {
        return RecyclerFragment.newInstance(
                ExploreFragment.TAG,
                RecyclerFragment.LIST_LAYOUT,
                RecyclerFragment.EXPLORE,
                false, position);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
