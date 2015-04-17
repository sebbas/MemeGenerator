package org.sebbas.android.memegenerator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class EditorFragment extends BaseFragment {

    private FrameLayout mScrollContainer;
    private ActionBarActivity mParentActivity;

    public static EditorFragment newInstance() {
        return new EditorFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mParentActivity = (ActionBarActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_editor, container, false);

        mParentActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        ViewCompat.setElevation(view.findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        //mScrollContainer = (FrameLayout) view.findViewById(R.id.scroll_container);

        //getChildFragmentManager().beginTransaction().replace(R.id.scroll_container, EditorCardsFragment.newInstance()).commit();

        return view;
    }
}
