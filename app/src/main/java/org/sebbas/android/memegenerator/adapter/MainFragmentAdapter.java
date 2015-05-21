package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.fragments.ExploreFragment;
import org.sebbas.android.memegenerator.fragments.GalleryFragment;
import org.sebbas.android.memegenerator.fragments.ImgurFragment;
import org.sebbas.android.memegenerator.fragments.SimplePreferenceFragment;
import org.sebbas.android.memegenerator.fragments.TemplatesFragment;

public class MainFragmentAdapter extends SlidingTabsFragmentAdapter {

    private static final String TAG = "MainViewPagerAdapter";

    private static final int mIcons[] = {
            R.drawable.selector_template_icon,
            R.drawable.selector_instances_icon,
            R.drawable.selector_explore_icon,
            R.drawable.selector_gallery_icon,
            R.drawable.selector_preferences_icon};

    private Context mContext;

    public MainFragmentAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
        mContext = context;
    }

    @Override
    protected Fragment createItem(int position) {
        switch (position) {
            case 0:
                return TemplatesFragment.newInstance();
            case 1:
                return ImgurFragment.newInstance(mContext);
            case 2:
                return ExploreFragment.newInstance();
            case 3:
                return GalleryFragment.newInstance(mContext);
            case 4:
                return SimplePreferenceFragment.newInstance();
            default:
                return TemplatesFragment.newInstance();
        }
    }

    public int getDrawableId(int position) {
        return mIcons[position];
    }
}
