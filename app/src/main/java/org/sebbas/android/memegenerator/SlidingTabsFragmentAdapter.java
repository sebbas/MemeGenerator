package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;

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

        switch (mFragment.getTitleResource()) {

            case R.string.instances:
                return getInstancesFragment(position);
            case R.string.gallery:
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
                id = ViewPagerRecyclerFragment.VIRAL;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            case 1:
                id = ViewPagerRecyclerFragment.TIME;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            case 2:
                id = ViewPagerRecyclerFragment.WINDOW_DAY;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            case 3:
                id = ViewPagerRecyclerFragment.WINDOW_WEEK;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            case 4:
                id = ViewPagerRecyclerFragment.WINDOW_MONTH;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            case 5:
                id = ViewPagerRecyclerFragment.WINDOW_YEAR;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            default:
                id = ViewPagerRecyclerFragment.VIRAL;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
        }
        //mFragmentManager.beginTransaction().add(fragment, Integer.toString(position)).commit();
        return fragment;
    }

    private Fragment getGalleryFragment(int position) {
        int id;
        int layout;
        BaseFragment fragment;

        switch (position) {
            case 0:
                id = ViewPagerRecyclerFragment.MY_MEMES;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            case 1:
                id = ViewPagerRecyclerFragment.RECENT;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            case 2:
                id = ViewPagerRecyclerFragment.FAVORITE_TEMPLATES;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            case 3:
                id = ViewPagerRecyclerFragment.FAVORITE_INSTANCES;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
            default:
                id = ViewPagerRecyclerFragment.VIRAL;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                break;
        }
        //mFragmentManager.beginTransaction().add(fragment, Integer.toString(position)).commit();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Log.d(TAG, "Destroying fragment");
        if (position <= getCount()) {
            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commit();
        }
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