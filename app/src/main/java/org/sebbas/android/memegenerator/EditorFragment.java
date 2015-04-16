package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EditorFragment extends BaseFragment {

    private ActionBarActivity mParentActivity;

    public static EditorFragment newInstance() {
        return new EditorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = (ActionBarActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        ViewCompat.setElevation(view.findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));

        mParentActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        // Set top and bottom padding dynamically (needed because of getActionBarSize())
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        view.findViewById(R.id.card_wrapper).setPadding(0, getActionBarSize(), 0, tabHeight);

        return view;
    }

    @Override
    void updateAdapter() {

    }
}
