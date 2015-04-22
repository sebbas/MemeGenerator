package org.sebbas.android.memegenerator;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Data {

    static final String CLIENT_ID = "";
    static final String THUMBNAIL_SIZE = "b";

    // Base url
    private static final String BASE = "https://api.imgur.com/3/g/memes/";

    // Sorting
    private static final String SORT_VIRAL = "viral/";
    private static final String SORT_TIME = "time/";
    private static final String SORT_TOP = "top/";

    // Window
    private static final String WINDOW_DAY = "day/";
    private static final String WINDOW_WEEK = "week/";
    private static final String WINDOW_MONTH = "month/";
    private static final String WINDOW_YEAR = "year/";
    private static final String WINDOW_ALL = "all/";

    private Data() {
        // No instances.
    }

    static final String getUrlForQuery(int pageIndex, String query) {
        return BASE + SORT_TOP + WINDOW_DAY + pageIndex + "/q=" + query.replace(" ", "&");
    }

    static final String getUrlForData(int pageIndex, int urlType) {
        switch (urlType) {
            case ViewPagerRecyclerViewFragment.VIRAL:
                return BASE + SORT_VIRAL + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_DAY:
                return BASE + SORT_TIME + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_WEEK:
                return BASE + SORT_TOP + WINDOW_WEEK + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_MONTH:
                return BASE + SORT_TOP + WINDOW_MONTH + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_YEAR:
                return BASE + SORT_TOP + WINDOW_YEAR + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_ALL:
                return BASE + SORT_TOP + WINDOW_ALL + pageIndex;
            default:
                return BASE + SORT_TOP + WINDOW_DAY + pageIndex;
        }
    }

    public static void saveSettings(int fragmentType, List<String> data) {
        Context context = MemeGeneratorApplication.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(Integer.toString(fragmentType), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> set = new HashSet<String>();
        set.addAll(data);
        editor.putStringSet("imageUrls", set);
        editor.commit();
    }

    public static ArrayList<String> loadSettings(int fragmentType) {
        Context context = MemeGeneratorApplication.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(Integer.toString(fragmentType), Context.MODE_PRIVATE);
        Set<String> set = preferences.getStringSet("imageUrls", null);

        if (set == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(set);
    }
}