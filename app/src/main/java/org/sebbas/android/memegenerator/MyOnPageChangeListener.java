package org.sebbas.android.memegenerator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

public class MyOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

    private ViewPagerAdapter mAdapter;
    private Context mContext;

    public MyOnPageChangeListener(ViewPagerAdapter adapter, Context context) {
        mAdapter = adapter;
        mContext = context;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        Fragment f = mAdapter.getItem(position);

        if (f instanceof BaseFragment) {
            ((BaseFragment)f).updateAdapter();
        }
    }
}
