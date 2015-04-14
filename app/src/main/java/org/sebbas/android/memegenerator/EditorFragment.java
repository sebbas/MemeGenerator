package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EditorFragment extends BaseFragment {

    public static EditorFragment newInstance() {
        return new EditorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        ActionBarActivity parentActivity = (ActionBarActivity) getActivity();
        parentActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        return view;
    }

    @Override
    void updateAdapter() {

    }
}
