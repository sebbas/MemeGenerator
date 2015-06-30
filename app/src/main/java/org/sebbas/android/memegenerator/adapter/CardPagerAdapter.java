package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.activities.EditorActivity;
import org.sebbas.android.memegenerator.fragments.CardFragment;

public class CardPagerAdapter extends SlidingTabsAdapter {

    private static final String TAG = "CardPagerAdapter";
    private Context mContext;

    public CardPagerAdapter(Context context, FragmentManager fragmentManager, String[] titles) {
        super(fragmentManager, titles);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        LineItem lineItem = ((EditorActivity) mContext).getLineItemAt(position);
        return CardFragment.newInstance(lineItem);
    }
}
