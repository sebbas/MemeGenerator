package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.fragments.ExploreFragment;
import org.sebbas.android.memegenerator.fragments.GalleryFragment;
import org.sebbas.android.memegenerator.fragments.ImgurFragment;
import org.sebbas.android.memegenerator.fragments.SimplePreferenceFragment;
import org.sebbas.android.memegenerator.fragments.TemplatesFragment;

public class MainActivityAdapter extends SlidingTabsAdapter {

    private static final String TAG = "MainViewPagerAdapter";

    private static final int mIcons[] = {
            R.drawable.selector_template_icon,
            R.drawable.selector_instances_icon,
            R.drawable.selector_explore_icon,
            R.drawable.selector_gallery_icon,
            R.drawable.selector_preferences_icon};

    private Context mContext;

    public MainActivityAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
        mContext = context;
    }

    @Override
    protected Fragment createItem(int position) {
        switch (position) {
            case 0:
                return TemplatesFragment.newInstance(UIOptions.SUPER_SLIM_LAYOUT, false);
            case 1:
                return ImgurFragment.newInstance();
            case 2:
                return ExploreFragment.newInstance(UIOptions.LIST_LAYOUT, false);
            case 3:
                return GalleryFragment.newInstance();
            case 4:
                return SimplePreferenceFragment.newInstance();
            default:
                return TemplatesFragment.newInstance(UIOptions.LIST_LAYOUT, false);
        }
    }

    public int getDrawableId(int position) {
        return mIcons[position];
    }
}
