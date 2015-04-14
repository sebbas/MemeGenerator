package org.sebbas.android.memegenerator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.samples.apps.iosched.ui.widget.SlidingTabLayoutForIcons;


public class MainActivity extends ActionBarActivity {

    private NonSwipeableViewPager mNonSwipeableViewPager;
    private ViewPagerAdapter mAdapter;
    private SlidingTabLayoutForIcons mSlidingTabs;
    private CharSequence mTitles[] = {"Memes", "Editor", "Gallery", "Preferences"};
    private int mIcons[] = {R.drawable.selector_meme_icon,
                            R.drawable.selector_editor_icon,
                            R.drawable.selector_gallery_icon,
                            R.drawable.selector_preferences_icon};
    private int mNumbOfTabs = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter =  new ViewPagerAdapter(getSupportFragmentManager(), mTitles, mIcons, mNumbOfTabs);

        mNonSwipeableViewPager = (NonSwipeableViewPager) findViewById(R.id.nonswipeable_viewpager);
        mNonSwipeableViewPager.setAdapter(mAdapter);

        mSlidingTabs = (SlidingTabLayoutForIcons) findViewById(R.id.sliding_tabs);
        mSlidingTabs.setDistributeEvenly(true);
        mSlidingTabs.setCustomTabColorizer(new SlidingTabLayoutForIcons.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });

        mSlidingTabs.setOnPageChangeListener(new MyOnPageChangeListener(mAdapter, this));
        mSlidingTabs.setViewPager(mNonSwipeableViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}