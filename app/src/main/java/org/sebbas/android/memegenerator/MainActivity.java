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
                            R.drawable.selector_preferences_icon,
                            R.drawable.selector_template_icon};
    private int mNumbOfTabs = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCustomLollipopActionBar();

        mMainViewPagerAdapter =  new MainViewPagerAdapter(
                getSupportFragmentManager(), mIcons, mNumbOfTabs);

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
    }

    @Override
    public void onItemClick(int position, List<LineItem> lineItems) {
        Intent editorIntent = new Intent(this, EditorActivity.class);

        LineItem lineItem = lineItems.get(position);
        String imageUrl = lineItem.getImageUrl();

        editorIntent.putExtra("imageUrl", imageUrl);
        startActivityForResult(editorIntent, 1);
    }
}