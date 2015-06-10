package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public abstract class SimpleFragment extends BaseFragment {

    private static final String TAG = "SimpleFragment";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.unregisterToolbarCallback();
    }

    /*
     * Toolbar Callbacks
     */
    /*@Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        RecyclerViewListener recyclerViewListener = mRootFragment;
        recyclerViewListener.filterDataWith(s);
        return true;
    }

    @Override
    public void onRefreshClicked() {

    }*/

}
