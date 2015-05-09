package org.sebbas.android.memegenerator;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class UIOptions {

    static final String THUMBNAIL_SIZE_LIST = "m";
    static final String THUMBNAIL_SIZE_CARD = "l";

    // Meme fragment recycler view layout
    static final int GRID_LAYOUT = 0;
    static final int LIST_LAYOUT = 1;
    static final int CARD_LAYOUT = 2;
    static final int SCROLLBOX_LAYOUT = 3;

    private static final int GRID_COLUMN_COUNT = 2;

    public static int getLayoutMode(int id) {
        switch (id) {
            case ViewPagerRecyclerFragment.VIRAL:
                return CARD_LAYOUT;
            case ViewPagerRecyclerFragment.TIME:
                return CARD_LAYOUT;
            case ViewPagerRecyclerFragment.WINDOW_DAY:
                return CARD_LAYOUT;
            case ViewPagerRecyclerFragment.WINDOW_WEEK:
                return CARD_LAYOUT;
            case ViewPagerRecyclerFragment.WINDOW_MONTH:
                return CARD_LAYOUT;
            case ViewPagerRecyclerFragment.WINDOW_YEAR:
                return CARD_LAYOUT;
            case ViewPagerRecyclerFragment.WINDOW_ALL:
                return CARD_LAYOUT;
            case ViewPagerRecyclerFragment.DEFAULTS:
                return LIST_LAYOUT;
            case ViewPagerRecyclerFragment.MY_MEMES:
                return LIST_LAYOUT;
            case ViewPagerRecyclerFragment.RECENT:
                return LIST_LAYOUT;
            case ViewPagerRecyclerFragment.FAVORITE_TEMPLATES:
                return LIST_LAYOUT;
            case ViewPagerRecyclerFragment.FAVORITE_INSTANCES:
                return LIST_LAYOUT;
            case ViewPagerRecyclerFragment.EXPLORE:
                return SCROLLBOX_LAYOUT;
            default:
                return LIST_LAYOUT;
        }
    }

    public static int getGridColumnCount() {
        return GRID_COLUMN_COUNT;
    }

    /*public static int getScreenWidth() {
        Context context = MemeGeneratorApplication.getAppContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }*/

    public static int getCardImageHeight() {
        return getCardImageWidth();
    }

    public static int getCardImageWidth() {
        return 500;
    }
}
