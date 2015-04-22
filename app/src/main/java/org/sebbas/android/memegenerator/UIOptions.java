package org.sebbas.android.memegenerator;

public class UIOptions {

    // Meme fragment recycler view layout
    static final int GRID_LAYOUT = 0;
    static final int LIST_LAYOUT = 1;
    static final int CARD_LAYOUT = 2;

    public static int getLayoutMode() {
        return GRID_LAYOUT;
    }
}
