package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class EditorActivity extends BaseActivity {

    private String mImageUrl;
    private ImageView mMemeImage;
    private Picasso mPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        setCustomLollipopActionBar();

        mImageUrl = getIntent().getStringExtra("imageUrl");
        mMemeImage = (ImageView) findViewById(R.id.item_image);

        // Initialize Picasso object with okhttp
        OkHttpClient okHttpClient = new OkHttpClient();
        mPicasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();

        // Set support toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Customize toolbar: back arrow and title text
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.editor);

        ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));

        // Hide keyboard when activity starts
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPicasso.with(this) //
                .load(mImageUrl) //
                .placeholder(android.R.color.white) //
                .error(android.R.color.white) //
                .tag(this) //
                .fit()
                .centerInside()
                .into(mMemeImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
