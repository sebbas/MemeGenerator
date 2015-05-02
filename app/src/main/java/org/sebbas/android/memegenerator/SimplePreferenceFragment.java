package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SimplePreferenceFragment extends BaseFragment implements ToolbarCallback {

    private static final String TAG = "SimplePreferenceFragment";

    public static SimplePreferenceFragment newInstance() {
        return new SimplePreferenceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        // Get a preferences fragment
        PreferencesFragment fragment = PreferencesFragment.newInstance();

        // Attach the just obtained fragment to frame layout
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.preferences_container, fragment);
        fragmentTransaction.commit();

        // Set padding for view so that top and bottom bars don't interfere
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        view.findViewById(R.id.preferences_container).setPadding(0, getActionBarSize(), 0, tabHeight);

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
        return false;
    }

    @Override
    public void onRefreshClicked() {
    }
}
