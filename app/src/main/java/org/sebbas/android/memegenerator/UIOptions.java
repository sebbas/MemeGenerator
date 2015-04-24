package org.sebbas.android.memegenerator;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

public class UIOptions {

    // Meme fragment recycler view layout
    static final int GRID_LAYOUT = 0;
    static final int LIST_LAYOUT = 1;
    static final int CARD_LAYOUT = 2;

    private static final int GRID_COLUMN_COUNT = 2;

    public static int getLayoutMode(ActionBarActivity activity) {
        if (activity instanceof SearchActivity) {
            return LIST_LAYOUT;
        }
        return LIST_LAYOUT;
    }

    public static int getLayoutMode(Fragment fragment) {
        if (fragment instanceof SimpleFragment) {
            return LIST_LAYOUT;
        }
        if (fragment instanceof SlidingTabsFragment) {
            return CARD_LAYOUT;
        }
        return LIST_LAYOUT;
    }

    public static int getGridColumnCount() {
        return GRID_COLUMN_COUNT;
    }
}
