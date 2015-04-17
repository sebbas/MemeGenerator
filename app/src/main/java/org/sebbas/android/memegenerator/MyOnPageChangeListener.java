package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

public class MyOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

    private ViewPagerAdapter mAdapter;

    public MyOnPageChangeListener(ViewPagerAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        Fragment f = mAdapter.getItem(position);

        if (f instanceof TemplateFragment) {
            //((MemeFragment)f).updateAdapter();
        }
    }
}