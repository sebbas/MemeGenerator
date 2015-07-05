package org.sebbas.android.memegenerator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.support.v7.widget.RecyclerView.OnScrollListener;

public class FastScroller extends LinearLayout {
    private static final String TAG = "FastScroller";
    private static final int BUBBLE_ANIMATION_DURATION = 100;
    private static final int TRACK_SNAP_RANGE = 5;

    private TextView mBubble;
    private View mHandle;
    private RecyclerView mRecyclerView;
    private final ScrollListener mScrollListener = new ScrollListener();
    private int mHeight;
    private int mStartScrollPosition;

    private ObjectAnimator mCurrentAnimator = null;

    public FastScroller(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

    public FastScroller(final Context context) {
        super(context);
        initialise(context);
    }

    public FastScroller(final Context context,final AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    private void initialise(Context context) {
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.recycler_view_fast_scroller__fast_scroller,this,true);
        mBubble = (TextView)findViewById(R.id.fastscroller_bubble);
        mHandle = findViewById(R.id.fastscroller_handle);
        mBubble.setVisibility(INVISIBLE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < mHandle.getX()) {
                    return false;
                }
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                if (mBubble.getVisibility() == INVISIBLE) {
                    showBubble();
                }
                mHandle.setSelected(true);
            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                setBubbleAndHandlePosition(y);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHandle.setSelected(false);
                hideBubble();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        recyclerView.addOnScrollListener(mScrollListener);
    }

    private void setRecyclerViewPosition(float y) {
        if(mRecyclerView != null) {
            int itemCount = mRecyclerView.getAdapter().getItemCount();
            float proportion;
            if (mHandle.getY() == 0) {
                proportion = 0f;
            } else if (mHandle.getY() + mHandle.getHeight() >= mHeight -TRACK_SNAP_RANGE) {
                proportion = 1f;
            } else {
                proportion = y / (float) mHeight;
            }
            int targetPos = getValueInRange(mStartScrollPosition, itemCount - 1, (int) (proportion * (float) itemCount));
            Log.d(TAG, "targetPos: " + targetPos);
            mRecyclerView.getLayoutManager().scrollToPosition(targetPos);
            String bubbleText = ((BubbleTextGetter) mRecyclerView.getAdapter()).getTextToShowInBubble(targetPos);
            mBubble.setText(bubbleText);
        }
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    private void setBubbleAndHandlePosition(float y) {
        int bubbleHeight = mBubble.getHeight();
        int handleHeight = mHandle.getHeight();
        mHandle.setY(getValueInRange(0, mHeight - handleHeight, (int) (y - handleHeight / 2)));
        mBubble.setY(getValueInRange(0, mHeight - bubbleHeight - handleHeight / 2, (int) (y - bubbleHeight)));
    }

    private void showBubble() {
        mBubble.setVisibility(VISIBLE);
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        mCurrentAnimator = ObjectAnimator.ofFloat(mBubble, "alpha", 0f, 1f).setDuration(BUBBLE_ANIMATION_DURATION);
        mCurrentAnimator.start();
    }

    private void hideBubble() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        mCurrentAnimator = ObjectAnimator.ofFloat(mBubble, "alpha", 1f, 0f).setDuration(BUBBLE_ANIMATION_DURATION);
        mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mBubble.setVisibility(INVISIBLE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mBubble.setVisibility(INVISIBLE);
                mCurrentAnimator = null;
            }
        });
        mCurrentAnimator.start();
    }

    private class ScrollListener extends OnScrollListener{
        @Override
        public void onScrolled(RecyclerView rv,int dx,int dy) {
            View firstVisibleView = mRecyclerView.getChildAt(0);
            int firstVisiblePosition = mRecyclerView.getChildLayoutPosition(firstVisibleView);
            int visibleRange = mRecyclerView.getChildCount();
            int lastVisiblePosition = firstVisiblePosition+visibleRange;
            int itemCount = mRecyclerView.getAdapter().getItemCount();
            int position;
            if(firstVisiblePosition == 0) {
                position = 0;
            } else if (lastVisiblePosition == itemCount) {
                position = itemCount;
            } else {
                position = (int) (((float) firstVisiblePosition / (((float) itemCount - (float) visibleRange))) * (float) itemCount);
            }
            float proportion = (float) position / (float) itemCount;
            setBubbleAndHandlePosition(mHeight * proportion);
        }
    }

    public void setStartScrollPosition(int startScrollPosition) {
        mStartScrollPosition = startScrollPosition;
    }
}
