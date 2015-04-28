package org.sebbas.android.memegenerator;

public class UIOptions {

    static final String THUMBNAIL_SIZE_LIST = "m";
    static final String THUMBNAIL_SIZE_CARD = "l";

    // Meme fragment recycler view layout
    static final int GRID_LAYOUT = 0;
    static final int LIST_LAYOUT = 1;
    static final int CARD_LAYOUT = 2;

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
            default:
                return LIST_LAYOUT;
        }
    }

    public static int getGridColumnCount() {
        return GRID_COLUMN_COUNT;
    }
}
