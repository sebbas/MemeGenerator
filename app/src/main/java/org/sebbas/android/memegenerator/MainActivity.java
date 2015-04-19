package org.sebbas.android.memegenerator;

import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.samples.apps.iosched.ui.widget.SlidingTabLayoutForIcons;

public class MainActivity extends ActionBarActivity implements ItemClickCallback {

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private DiskLruImageCache mDiskLruImageCache;

    private NonSwipeableViewPager mMainViewPager;
    private MainViewPagerAdapter mMainViewPagerAdapter;
    private SlidingTabLayoutForIcons mSlidingTabs;
    private int mIcons[] = {R.drawable.selector_template_icon,
                            R.drawable.selector_instances_icon,
                            R.drawable.selector_gallery_icon,
                            R.drawable.selector_preferences_icon};
    private int mNumbOfTabs = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Customize task bar which is visible in multitask mode in Android Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setTaskDescription(new ActivityManager.TaskDescription(null, null, getResources().getColor(R.color.primaryDark)));
        }

        mMainViewPagerAdapter =  new MainViewPagerAdapter(getSupportFragmentManager(), mIcons, mNumbOfTabs);

        mMainViewPager = (NonSwipeableViewPager) findViewById(R.id.nonswipeable_viewpager);
        mMainViewPager.setAdapter(mMainViewPagerAdapter);

        mSlidingTabs = (SlidingTabLayoutForIcons) findViewById(R.id.sliding_tabs);
        mSlidingTabs.setDistributeEvenly(true);
        mSlidingTabs.setCustomTabColorizer(new SlidingTabLayoutForIcons.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });

        mSlidingTabs.setViewPager(mMainViewPager);

        // Instantiate image cache
        mDiskLruImageCache = new DiskLruImageCache(this, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE, Bitmap.CompressFormat.JPEG, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        ComponentName cn = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position, DataLoader dataLoader) {
        Intent editorIntent = new Intent(this, EditorActivity.class);

        String imageUrl = dataLoader.getImageUrlAt(position);

        editorIntent.putExtra("imageUrl", imageUrl);
        startActivityForResult(editorIntent, 1);
    }

    public DiskLruImageCache getDiskLruImageCache() {
        return mDiskLruImageCache;
    }
}