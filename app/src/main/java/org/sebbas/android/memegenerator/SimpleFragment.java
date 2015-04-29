package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SimpleFragment extends BaseFragment implements ToolbarCallback{

    private static final String TAG = "SimpleFragment";
    private ViewPagerRecyclerFragment mRootFragment;

    public static SimpleFragment newInstance(int titleResource) {
        SimpleFragment simpleFragment = new SimpleFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_title", titleResource);
        simpleFragment.setArguments(args);
        return simpleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_simple, container, false);
        setHasOptionsMenu(true);

        // Setup the toolbar
        int titleResource = getArguments().getInt("fragment_title");
        int menuResource = R.menu.menu_simple_fragment;
        setupToolbar(this, view, titleResource, menuResource);

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
