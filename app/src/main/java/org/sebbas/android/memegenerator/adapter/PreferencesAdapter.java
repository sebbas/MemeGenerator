package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.fragments.PreferencesFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

import java.util.List;

public class PreferencesAdapter extends RecyclerFragmentAdapter {

    private static final int VIEW_TYPE_FILLER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private BaseFragment mFragment;

    public PreferencesAdapter(BaseFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FILLER : VIEW_TYPE_CONTENT;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mFragment.getActivity());

        if (viewType == VIEW_TYPE_FILLER) {
            view = inflater.inflate(R.layout.recycler_header_small, parent, false);
        } else {
            view = inflater.inflate(R.layout.fragment_preferences, parent, false);
        }

        return new PreferencesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder viewHolder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {

            PreferencesFragment preferencesFragment = PreferencesFragment.newInstance();
            int id = ((PreferencesViewHolder) viewHolder).preferencesContainer.getId();

            FragmentTransaction fragmentTransaction = mFragment.getFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, preferencesFragment);
            fragmentTransaction.commit();

        }
    }

    static class PreferencesViewHolder extends MainViewHolder {
        FrameLayout preferencesContainer;

        public PreferencesViewHolder(View view) {
            super(view);
            this.preferencesContainer = (FrameLayout) view.findViewById(R.id.preferences_container);
        }
    }

    @Override
    protected List<LineItem> getLineItems() {
        return null;
    }

    @Override
    public void refreshUI() {
    }

    @Override
    public void refreshData() {
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
