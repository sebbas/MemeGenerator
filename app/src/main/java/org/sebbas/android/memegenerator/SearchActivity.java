package org.sebbas.android.memegenerator;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class SearchActivity extends BaseActivity implements ItemClickCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setCustomLollipopActionBar();

        // Set support toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Customize toolbar: back arrow and title text
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        handleIntent(getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // Set toolbar title that contains search query
            String toolbarTitle = getResources().getString(R.string.search) + " " + query;
            getSupportActionBar().setTitle(toolbarTitle);

            // Create a fragment that loads the query and data
            int id = ViewPagerRecyclerViewFragment.SEARCH;
            int layout = UIOptions.getLayoutMode(id);
            ViewPagerRecyclerViewFragment searchContainer =
                    ViewPagerRecyclerViewFragment.newInstance(id, layout, query);

            // Finally add the search container to the UI
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.search_container, searchContainer);
            transaction.commit();
        }
    }
}
