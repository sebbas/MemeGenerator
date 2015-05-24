package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public class SimplePreferenceFragment extends SimpleFragment implements ToolbarCallback {

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

    @Override
    public void onBackPressed() {

    }

    @Override
    void setupFragmentToolbarAt(int position) {
        int titleResource = R.string.preferences;
        int menuResource = R.menu.menu_simple_fragment;

        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.setupToolbar(titleResource, menuResource, true);
    }

    @Override
    void registerFragmentToolbarCallbacks(int position) {
        ((BaseActivity) getActivity()).registerToolbarCallback(this);
    }
}
