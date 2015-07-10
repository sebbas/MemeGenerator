package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;

public class PlaceholderFragment extends BaseFragment {

    private static final String TAG = "PlaceholderFragment";

    public static PlaceholderFragment newInstance() {
        return new PlaceholderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_placeholder, container, false);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public boolean isVisibleToUser() {
        return false;
    }
}
