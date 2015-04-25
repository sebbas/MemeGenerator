package org.sebbas.android.memegenerator;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.samples.apps.iosched.ui.widget.SlidingTabLayoutForIcons;

public class MainActivity extends BaseActivity implements ItemClickCallback {

    private NonSwipeableViewPager mMainViewPager;
    private MainViewPagerAdapter mMainViewPagerAdapter;
    private SlidingTabLayoutForIcons mMainTabs;
    private int mIcons[] = {R.drawable.selector_template_icon,
                            R.drawable.selector_instances_icon,
                            R.drawable.selector_gallery_icon,
                            R.drawable.selector_preferences_icon};
    private int mNumbOfTabs = 4;

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

}