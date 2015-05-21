package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;
import org.sebbas.android.memegenerator.UIOptions;

public class ExploreFragment extends SimpleFragment implements ToolbarCallback {

    private static final String TAG = "ExploreFragment";

    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_simple, container, false);

        // Get a recycler fragment
        int id = RecyclerFragment.EXPLORE;
        int layout = UIOptions.getLayoutMode(id);
        RecyclerFragment rootFragment = RecyclerFragment.newInstance(id, layout, false);

        // Attach the just obtained fragment to frame layout
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_container, rootFragment);
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
        int titleResource = R.string.explore;
        int menuResource = R.menu.menu_simple_fragment;

        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.setupToolbar(parentActivity, titleResource, menuResource, false);
    }

    @Override
    void registerFragmentToolbarCallbacks(int position) {
        ((BaseActivity) getActivity()).registerToolbarCallback(this);
    }
}
