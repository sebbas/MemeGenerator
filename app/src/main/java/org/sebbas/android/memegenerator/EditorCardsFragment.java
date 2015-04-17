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

        // Set top padding dynamically (because we get actionbar height dynamically)
        view.findViewById(R.id.cards_scrollview).setPadding(0, getActionBarSize(), 0, 0);
        return view;
    }
}
