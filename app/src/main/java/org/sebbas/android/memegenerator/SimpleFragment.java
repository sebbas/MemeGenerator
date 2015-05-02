package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SimpleFragment extends BaseFragment implements ToolbarCallback{

    private static final String TAG = "SimpleFragment";
    private ViewPagerRecyclerFragment mRootFragment;

    public static SimpleFragment newInstance() {
        return new SimpleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_simple, container, false);

        // Get a recycler fragment
        int id = ViewPagerRecyclerFragment.DEFAULTS;
        int layout = UIOptions.getLayoutMode(id);
        mRootFragment = ViewPagerRecyclerFragment.newInstance(id, layout);

        // Attach the just obtained fragment to frame layout
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.recycler_container, mRootFragment);
        fragmentTransaction.commit();

        // Set padding for view so that top and bottom bars don't interfere
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        view.findViewById(R.id.recycler_container).setPadding(0, getActionBarSize(), 0, tabHeight);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.unregisterToolbarCallback();
    }

    /*
     * Toolbar Callbacks
     */
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        RecyclerViewListener recyclerViewListener = mRootFragment;
        recyclerViewListener.filterAdapterWith(s);
        return true;
    }

    @Override
    public void onRefreshClicked() {

    }
}
