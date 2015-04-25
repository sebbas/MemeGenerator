package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SimpleFragment extends BaseFragment {

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

        // Setup the toolbar
        ActionBarActivity parentActivity = (ActionBarActivity) getActivity();
        parentActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        ViewCompat.setElevation(view.findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));

        // Set custom fragment title
        int titleResource = getArguments().getInt("fragment_title");
        setActionBarTitle(titleResource);

        // Get a recycler fragment
        int id = ViewPagerRecyclerViewFragment.DEFAULTS;
        int layout = UIOptions.getLayoutMode(id);
        ViewPagerRecyclerViewFragment fragment = ViewPagerRecyclerViewFragment.newInstance(id, layout);

        // Attach the just obtained fragment to frame layout
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.recycler_container, fragment);
        fragmentTransaction.commit();

        // Set padding for view so that top and bottom bars don't interfere
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        view.findViewById(R.id.recycler_container).setPadding(0, getActionBarSize(), 0, tabHeight);

        // Set custom title
        String toolbarTitle = getResources().getString(R.string.templates);
        setActionBarTitle(toolbarTitle);

        return view;
    }
}
