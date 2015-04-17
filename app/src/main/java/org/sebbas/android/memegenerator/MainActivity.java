package org.sebbas.android.memegenerator;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;

import com.google.samples.apps.iosched.ui.widget.SlidingTabLayoutForIcons;

public class MainActivity extends ActionBarActivity implements ListCallback {

    private NonSwipeableViewPager mNonSwipeableViewPager;
    private MainViewPagerAdapter mAdapter;
    private SlidingTabLayoutForIcons mSlidingTabs;
    private int mIcons[] = {R.drawable.selector_meme_icon,
                            R.drawable.selector_editor_icon,
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

        mAdapter =  new MainViewPagerAdapter(getSupportFragmentManager(), mIcons, mNumbOfTabs);

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

    @Override
    public void onItemClick(int position, BaseAdapter baseAdapter) {
        Intent editorIntent = new Intent(this, EditorActivity.class);

        GoogleCardsAdapter googleCardsAdapter = (GoogleCardsAdapter) baseAdapter;
        String imageUrl = googleCardsAdapter.getImageUrlAt(position);

        editorIntent.putExtra("imageUrl", imageUrl);
        startActivityForResult(editorIntent, 1);
    }
}