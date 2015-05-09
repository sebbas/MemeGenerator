package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class ExploreFragment extends BaseFragment implements ToolbarCallback {


    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // Get a recycler fragment
        int id = ViewPagerRecyclerFragment.EXPLORE;
        int layout = UIOptions.getLayoutMode(id);
        ViewPagerRecyclerFragment rootFragment = ViewPagerRecyclerFragment.newInstance(id, layout, false);

        // Attach the just obtained fragment to frame layout
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.recycler_container_topics, rootFragment);
        fragmentTransaction.commit();

        // Set padding for view so that top and bottom bars don't interfere
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        view.findViewById(R.id.recycler_container_topics).setPadding(0, getActionBarSize(), 0, tabHeight);

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onRefreshClicked() {

    }
}
