package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.Map;

public class SlidingTabsFragmentAdapter extends CacheFragmentStatePagerAdapter {

    private SlidingTabsFragment mFragment;
    private Map<Integer, Fragment> mPageReferenceMap;
    private FragmentManager mFragmentManager;

    public SlidingTabsFragmentAdapter(Fragment fragment, FragmentManager fragmentManager) {
        super(fragmentManager);

        mFragment = (SlidingTabsFragment) fragment;
        mPageReferenceMap = new HashMap<>();
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
                //mPageReferenceMap.put(0, fragment);
                break;
            case 1:
                id = ViewPagerRecyclerFragment.TIME;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(1, fragment);
                break;
            case 2:
                id = ViewPagerRecyclerFragment.WINDOW_DAY;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(2, fragment);
                break;
            case 3:
                id = ViewPagerRecyclerFragment.WINDOW_WEEK;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(3, fragment);
                break;
            case 4:
                id = ViewPagerRecyclerFragment.WINDOW_MONTH;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(4, fragment);
                break;
            case 5:
                id = ViewPagerRecyclerFragment.WINDOW_YEAR;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(5, fragment);
                break;
            default:
                id = ViewPagerRecyclerFragment.VIRAL;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(10, fragment);
                break;
        }
        mFragmentManager.beginTransaction().add(fragment, Integer.toString(position));
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
                //mPageReferenceMap.put(6, fragment);
                break;
            case 1:
                id = ViewPagerRecyclerFragment.RECENT;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(7, fragment);
                break;
            case 2:
                id = ViewPagerRecyclerFragment.FAVORITE_TEMPLATES;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(8, fragment);
                break;
            case 3:
                id = ViewPagerRecyclerFragment.FAVORITE_INSTANCES;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(9, fragment);
                break;
            default:
                id = ViewPagerRecyclerFragment.VIRAL;
                layout = UIOptions.getLayoutMode(id);
                fragment = ViewPagerRecyclerFragment.newInstance(id, layout);
                //mPageReferenceMap.put(10, fragment);
                break;
        }
        mFragmentManager.beginTransaction().add(fragment, Integer.toString(position));
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        //mPageReferenceMap.remove(position);

        Fragment fragment = mFragmentManager.getFragments().get(position);
        mFragmentManager.beginTransaction().remove(fragment);
    }

    public Fragment getFragment(String id) {
        //return mPageReferenceMap.get(id);
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