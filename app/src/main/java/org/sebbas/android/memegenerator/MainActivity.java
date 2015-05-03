package org.sebbas.android.memegenerator;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayoutForIcons;
import com.tonicartos.superslim.LayoutManager;

import java.util.List;

public class MainActivity extends BaseActivity implements ItemClickCallback {

    private static final String TAG = "MainActivity";

    private NonSwipeableViewPager mMainViewPager;
    private FragmentStatePagerAdapter mMainViewPagerAdapter;
    private SlidingTabLayoutForIcons mMainTabs;
    private OrientationEventListener mOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCustomLollipopActionBar();

        mMainViewPagerAdapter =  new MainViewPagerAdapter(getSupportFragmentManager());

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
        mMainTabs.setViewPager(mMainViewPager);
        mMainTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setupFragmentToolbars(position);
                registerFragmentToolbarCallbacks(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Listen for orientation changes
        mOrientationListener = new OrientationEventListener(getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        400, LinearLayout.LayoutParams.MATCH_PARENT);
                mMainTabs.setLayoutParams(layoutParams);
                mMainTabs.invalidate();*/
                mMainTabs = new SlidingTabLayoutForIcons(MainActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        400, LinearLayout.LayoutParams.MATCH_PARENT);
                mMainTabs.setLayoutParams(layoutParams);
            }
        };
        mOrientationListener.enable();

        // Initial toolbar setup at position 0
        setupFragmentToolbars(0);
        registerFragmentToolbarCallbacks(0);
    }

    private void setupFragmentToolbars(int position) {
        int titleResource = 0;
        int menuResource = 0;
        switch(position) {
            case 0:
                titleResource = R.string.templates;
                menuResource = R.menu.menu_simple_fragment;
                break;
            case 1:
                titleResource = R.string.instances;
                menuResource = R.menu.menu_sliding_tabs_fragment;
                break;
            case 2:
                titleResource = R.string.explore;
                break;
            case 3:
                titleResource = R.string.gallery;
                menuResource = R.menu.menu_sliding_tabs_fragment;
                break;
            case 4:
                titleResource = R.string.preferences;
                break;
            default:
                titleResource = R.string.app_name;
                menuResource = R.menu.menu_simple_fragment;
                break;
        }
        setupToolbar(this, titleResource, menuResource);
    }

    private void registerFragmentToolbarCallbacks(int position) {
        BaseFragment baseFragment = (BaseFragment) mMainViewPagerAdapter
                .instantiateItem(mMainViewPager, position);
        registerToolbarCallback(baseFragment);
    }

    /*
     * ItemClickCallback
     */
    @Override
    public void onItemClick(int position, List<LineItem> lineItems) {
        Intent editorIntent = new Intent(this, EditorActivity.class);

        LineItem lineItem = lineItems.get(position);
        String imageUrl = lineItem.getImageUrl();

        editorIntent.putExtra("imageUrl", imageUrl);
        startActivityForResult(editorIntent, 1);
    }

}