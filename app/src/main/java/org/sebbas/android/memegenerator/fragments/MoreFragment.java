package org.sebbas.android.memegenerator.fragments;

public class MoreFragment extends RecyclerFragment {

    public static final String TAG = "MoreFragment";

    public static RecyclerFragment newInstance(int position) {
        return RecyclerFragment.newInstance(RecyclerFragment.MORE_FRAGMENT, RecyclerFragment.LIST_LAYOUT, false, position);
    }
}
