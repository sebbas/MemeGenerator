package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EditorCardsFragment extends BaseFragment {

    public static EditorCardsFragment newInstance() {
        return new EditorCardsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor_cards, container, false);

        // Set top and bottom padding dynamically (needed because of getActionBarSize())
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        view.findViewById(R.id.cards_scrollview).setPadding(0, getActionBarSize(), 0, tabHeight);
        return view;
    }
}
