package org.sebbas.android.memegenerator;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ToggleSwipeViewPager extends ViewPager {
    private boolean mSwipeEnabled;

    public ToggleSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSwipeEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSwipeEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mSwipeEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    /**
     * Custom implementation to enable or not swipe :)
     *
     * @param enabled
     *            true to enable swipe, false otherwise.
     */
    public void setPagingEnabled(boolean enabled) {
        mSwipeEnabled = enabled;
    }

}