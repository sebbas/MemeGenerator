package org.sebbas.android.memegenerator.fragments;

public class MoreFragment extends RecyclerFragment {

    public static final String TAG = "MoreFragment";

    public static RecyclerFragment newInstance(int position) {
        boolean isRefreshable, isNetworkEnabled;

        isRefreshable = false;
        isNetworkEnabled = false;

        return RecyclerFragment.newInstance(
                MoreFragment.TAG,
                RecyclerFragment.LIST_LAYOUT,
                RecyclerFragment.SIMPLE,
                isRefreshable, position, isNetworkEnabled);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
