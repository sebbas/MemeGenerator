package org.sebbas.android.memegenerator;

public class UIOptions {

    public static final String THUMBNAIL_SIZE_LIST = "m";
    public static final String THUMBNAIL_SIZE_CARD = "l";

    public static final int GRID_LAYOUT = 0;
    public static final int LIST_LAYOUT = 1;
    public static final int SUPER_SLIM_LAYOUT = 2;

    private static final int GRID_COLUMN_COUNT = 2;

    public static int getGridColumnCount() {
        return GRID_COLUMN_COUNT;
    }
}
