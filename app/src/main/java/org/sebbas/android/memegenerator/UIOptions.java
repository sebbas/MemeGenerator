package org.sebbas.android.memegenerator;

import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

public class UIOptions {

    public static final String THUMBNAIL_SIZE_LIST = "m";
    public static final String THUMBNAIL_SIZE_CARD = "l";

    // Meme fragment recycler view layout
    public static final int GRID_LAYOUT = 0;
    public static final int LIST_LAYOUT = 1;
    public static final int CARD_LAYOUT = 2;
    public static final int SCROLLBOX_LAYOUT = 3;

    private static final int GRID_COLUMN_COUNT = 2;

    public static int getLayoutMode(int id) {
        switch (id) {
            case RecyclerFragment.ALL:
                return CARD_LAYOUT;
            case RecyclerFragment.MEMES:
                return CARD_LAYOUT;
            case RecyclerFragment.GIFS:
                return CARD_LAYOUT;
            case RecyclerFragment.DEFAULTS:
                return LIST_LAYOUT;
            case RecyclerFragment.MY_MEMES:
                return LIST_LAYOUT;
            case RecyclerFragment.RECENT:
                return LIST_LAYOUT;
            case RecyclerFragment.FAVORITE_TEMPLATES:
                return LIST_LAYOUT;
            case RecyclerFragment.FAVORITE_INSTANCES:
                return LIST_LAYOUT;
            case RecyclerFragment.EXPLORE:
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
