package org.sebbas.android.memegenerator;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpanCount;
    private int mSpacing;
    private boolean mIncludeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        mSpanCount = spanCount;
        mSpacing = spacing;
        mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position

        // Exclude the first item as it is spanning all columns and only needed for padding
        if (position != 0) {
            // The real first grid position because off padding item at position 0
            int positionReal = position - 1;
            int column = (positionReal) % mSpanCount; // item column

            if (mIncludeEdge) {
                outRect.left = mSpacing - column * mSpacing / mSpanCount;
                outRect.right = (column + 1) * mSpacing / mSpanCount;

                if (positionReal < mSpanCount) { // top edge
                    outRect.top = mSpacing;
                }
                outRect.bottom = mSpacing; // item bottom
            } else {
                outRect.left = column * mSpacing / mSpanCount;
                outRect.right = mSpacing - (column + 1) * mSpanCount / mSpanCount;
                if (positionReal >= mSpanCount) {
                    outRect.top = mSpacing; // item top
                }
            }
        }
    }
}