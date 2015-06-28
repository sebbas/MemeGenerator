package org.sebbas.android.memegenerator.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.fragments.CardFragment;
import org.sebbas.android.memegenerator.fragments.EditorFragment;

public class CardPagerAdapter extends SlidingTabsAdapter {

    private static final String TAG = "CardPagerAdapter";
    private EditorFragment mEditorFragment;

    public CardPagerAdapter(EditorFragment editorFragment, FragmentManager fragmentManager, String[] titles) {
        super(fragmentManager, titles);
        mEditorFragment = editorFragment;
    }

    @Override
    public Fragment getItem(int position) {
        LineItem lineItem = mEditorFragment.getLineItemAt(position);
        return CardFragment.newInstance(lineItem);
    }
}
