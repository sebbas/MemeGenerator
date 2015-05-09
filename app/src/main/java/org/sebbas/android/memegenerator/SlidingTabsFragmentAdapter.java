package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;


public class SlidingTabsFragmentAdapter extends CacheFragmentStatePagerAdapter {

    private static final String TAG = "SlidingTabsFragmentAdapter";
    private SlidingTabsFragment mFragment;
    private FragmentManager mFragmentManager;

    public SlidingTabsFragmentAdapter(Fragment fragment, FragmentManager fragmentManager) {
        super(fragmentManager);

        mFragment = (SlidingTabsFragment) fragment;
        mFragmentManager = fragmentManager;
    }

    @Override
    protected Fragment createItem(int position) {

        switch (mFragment.getParentViewPagerPosition()) {

            case MainViewPagerAdapter.INSTANCES_FRAGMENT_POSITION:
                return getInstancesFragment(position);
            case MainViewPagerAdapter.GALLERY_FRAGMENT_POSITION:
                return getGalleryFragment(position);
            default:
                return getInstancesFragment(position);
        }
    }

    private Fragment getInstancesFragment(int position) {
        int id;
        int layout;
        BaseFragment fragment;

        switch (position) {
            case 0:
                id = RecyclerFragment.ALL;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            case 1:
                id = RecyclerFragment.MEMES;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            case 2:
                id = RecyclerFragment.GIFS;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            default:
                id = RecyclerFragment.ALL;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
        }
        return fragment;
    }

    private Fragment getGalleryFragment(int position) {
        int id;
        int layout;
        BaseFragment fragment;

        switch (position) {
            case 0:
                id = RecyclerFragment.MY_MEMES;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            case 1:
                id = RecyclerFragment.RECENT;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            case 2:
                id = RecyclerFragment.FAVORITE_TEMPLATES;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            case 3:
                id = RecyclerFragment.FAVORITE_INSTANCES;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
            default:
                id = RecyclerFragment.ALL;
                layout = UIOptions.getLayoutMode(id);
                fragment = RecyclerFragment.newInstance(id, layout, true);
                break;
        }
        return fragment;
    }

    public Fragment getFragment(String id) {
        return mFragmentManager.findFragmentByTag(id);
    }

    @Override
    public int getCount() {
        return mFragment.getTitles().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragment.getTitles()[position];
    }
}