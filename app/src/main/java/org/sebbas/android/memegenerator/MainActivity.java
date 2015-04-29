package org.sebbas.android.memegenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.samples.apps.iosched.ui.widget.SlidingTabLayoutForIcons;

import java.util.List;

public class MainActivity extends BaseActivity implements ItemClickCallback {

    private static final String TAG = "MainActivity";

    private NonSwipeableViewPager mMainViewPager;
    private MainViewPagerAdapter mMainViewPagerAdapter;
    private SlidingTabLayoutForIcons mMainTabs;
    private int mIcons[] = {R.drawable.selector_template_icon,
                            R.drawable.selector_instances_icon,
                            R.drawable.selector_gallery_icon,
                            R.drawable.selector_preferences_icon};
    private int mNumbOfTabs = 4;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCustomLollipopActionBar();

        mMainViewPagerAdapter =  new MainViewPagerAdapter(
                this, getSupportFragmentManager(), mIcons, mNumbOfTabs);

        mMainViewPager = (NonSwipeableViewPager) findViewById(R.id.nonswipeable_viewpager);
        mMainViewPager.setAdapter(mMainViewPagerAdapter);

        // Always preload all pages in viewpager
        mMainViewPager.setOffscreenPageLimit(mMainViewPagerAdapter.getCount());

        mMainTabs = (SlidingTabLayoutForIcons) findViewById(R.id.main_tabs);
        mMainTabs.setDistributeEvenly(true);
        mMainTabs.setCustomTabColorizer(new SlidingTabLayoutForIcons.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });
        mMainTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "position is " + position);
                //invalidateFragmentMenus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mMainTabs.setViewPager(mMainViewPager);
        //invalidateFragmentMenus(mMainViewPager.getCurrentItem());

    }

    @Override
    public void onItemClick(int position, List<LineItem> lineItems) {
        Intent editorIntent = new Intent(this, EditorActivity.class);

        LineItem lineItem = lineItems.get(position);
        String imageUrl = lineItem.getImageUrl();

        editorIntent.putExtra("imageUrl", imageUrl);
        startActivityForResult(editorIntent, 1);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);


        // Actions handled in fragments
        Log.d(TAG, "onCreateOptionsMenu");

        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int currentPage = mMainViewPager.getCurrentItem();
        switch (currentPage) {
            case 0:
                Log.d(TAG, "case 0");
                menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                menu.findItem(R.id.menu_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                menu.findItem(R.id.menu_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                break;
            case 1:
                Log.d(TAG, "case 1");
                menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                menu.findItem(R.id.menu_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                menu.findItem(R.id.menu_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                break;
            case 2:
                Log.d(TAG, "case 2");
                menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                menu.findItem(R.id.menu_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                menu.findItem(R.id.menu_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                break;
            case 3:
                Log.d(TAG, "case 3");
                menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                menu.findItem(R.id.menu_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                menu.findItem(R.id.menu_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }*/

    private void invalidateFragmentMenus(int position) {
        for (int i = 0; i < mMainViewPagerAdapter.getCount(); i++) {
            Fragment fragment = mMainViewPagerAdapter.getItem(i);
            fragment.setHasOptionsMenu(i == position);
        }
        supportInvalidateOptionsMenu();
    }
}