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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        mParentActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        ViewCompat.setElevation(view.findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        mScrollContainer = (FrameLayout) view.findViewById(R.id.scroll_container);

        getChildFragmentManager().beginTransaction().replace(R.id.scroll_container, EditorCardsFragment.newInstance()).commit();

        return view;
    }

    /*@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SoftKeyboardHandledLinearLayout main = (SoftKeyboardHandledLinearLayout) mParentActivity.findViewById(R.id.keyboard_listen_layout);
        final LinearLayout footer = (LinearLayout) main.findViewById(R.id.footer);

        main.setOnSoftKeyboardVisibilityChangeListener(new SoftKeyboardHandledLinearLayout.SoftKeyboardVisibilityChangeListener() {
            @Override
            public void onSoftKeyboardShow() {
                Toast.makeText(mParentActivity, "Shown", Toast.LENGTH_SHORT).show();
                //footer.setVisibility(View.GONE);
            }

            @Override
            public void onSoftKeyboardHide() {
                Toast.makeText(mParentActivity, "Hide", Toast.LENGTH_SHORT).show();
                //footer.setVisibility(View.VISIBLE);
            }
        });
    }*/
}
