package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.PreferencesAdapter;

public class SimplePreferenceFragment extends RecyclerFragment {

    private static final String TAG = "SimplePreferenceFragment";

    private PreferencesAdapter mPreferencesAdapter;

    public static SimplePreferenceFragment newInstance() {
        SimplePreferenceFragment fragment = new SimplePreferenceFragment();
        Bundle args = new Bundle();
        args.putString("fragment_type", TAG);
        args.putInt("layout_mode", RecyclerFragment.LIST_LAYOUT);
        args.putBoolean("refreshable", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferencesAdapter = new PreferencesAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        super.init(view);
        super.with(mPreferencesAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onFragmentComplete(TAG);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
