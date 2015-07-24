package org.sebbas.android.memegenerator.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.OutlineEditText;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

import java.util.ArrayList;

public class EditActivity extends BaseActivity implements FragmentCallback, ToolbarCallback {

    public static final String TAG = "EditActivity";
    public static final String LINE_ITEM = "lineItem";

    private LineItem mLineItem;
    private ImageView mImageView;
    private OutlineEditText mEditTextOne;
    private OutlineEditText mEditTextTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Get line items and start position / current item in pager
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mLineItem = extras.getParcelable(LINE_ITEM);
            }
        } else {
            mLineItem = savedInstanceState.getParcelable(LINE_ITEM);
        }

        // This makes the actionbar in multitask mode in Android Lollipop look a bit nicer
        setCustomLollipopActionBar();

        mImageView = (ImageView) findViewById(R.id.item_image);
        mEditTextOne = (OutlineEditText) findViewById(R.id.text_one);
        mEditTextTwo = (OutlineEditText) findViewById(R.id.text_two);

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/impact.ttf");
        String topText = getResources().getString(R.string.top_text);
        String bottomText = getResources().getString(R.string.bottom_text);

        mEditTextOne.changeAll(0xFFFFFFFF, 0xFF000000, typeface, 36, topText);
        mEditTextTwo.changeAll(0xFFFFFFFF, 0xFF000000, typeface, 36, bottomText);


        Glide.with(this)
                .load(mLineItem.getImageUrl())
                .fitCenter()
                .into(mImageView);


        // Setup toolbar title
        super.setupToolbar(TAG);
        super.registerToolbarCallback(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LINE_ITEM, mLineItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentComplete(BaseFragment baseFragment) {
    }

    @Override
    public void onItemClick(int itemPosition, ArrayList<LineItem> lineItems) {
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onToolbarBackPressed() {
        this.onBackPressed();
    }

    @Override
    public int getMainPagerPosition() {
        return 0;
    }

    @Override
    public int getLastFragmentPositionMain() {
        return 0;
    }

    private class TextDragListener implements View.OnTouchListener {

        private int deltaX;
        private int deltaY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    deltaX = X - lParams.leftMargin;
                    deltaY = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - deltaX;
                    layoutParams.topMargin = Y - deltaY;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    break;
            }
            //mContainerTextOne.invalidate();
            return true;
        }
    }

    private void setTypeFace(Typeface typeFace) {
        mEditTextOne.changeTypeface(typeFace);
        mEditTextTwo.changeTypeface(typeFace);
    }

    private void setTextColor(int innerColor, int outlineColor) {
        mEditTextOne.changeTextColor(innerColor, outlineColor);
        mEditTextTwo.changeTextColor(innerColor, outlineColor);
    }



}
