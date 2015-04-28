package org.sebbas.android.memegenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class Data {

    // Base url
    private static final String BASE = "https://api.imgur.com/3/g/memes/";
    private static final String BASE_SEARCH = "https://api.imgur.com/3/gallery/search/";
    private static final String BASE_DEFAULTS = "https://api.imgur.com/3/memegen/defaults";

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
        return BASE_SEARCH + query.replace(" ", "&");
    }

    static final String getUrlForData(int pageIndex, int urlType) {
        switch (urlType) {
            case ViewPagerRecyclerFragment.VIRAL:
                return BASE + SORT_VIRAL + pageIndex;
            case ViewPagerRecyclerFragment.TIME:
                return BASE + SORT_TIME + pageIndex;
            case ViewPagerRecyclerFragment.WINDOW_DAY:
                return BASE + SORT_TOP + WINDOW_DAY + pageIndex;
            case ViewPagerRecyclerFragment.WINDOW_WEEK:
                return BASE + SORT_TOP + WINDOW_WEEK + pageIndex;
            case ViewPagerRecyclerFragment.WINDOW_MONTH:
                return BASE + SORT_TOP + WINDOW_MONTH + pageIndex;
            case ViewPagerRecyclerFragment.WINDOW_YEAR:
                return BASE + SORT_TOP + WINDOW_YEAR + pageIndex;
            case ViewPagerRecyclerFragment.WINDOW_ALL:
                return BASE + SORT_TOP + WINDOW_ALL + pageIndex;
            case ViewPagerRecyclerFragment.DEFAULTS:
                return BASE_DEFAULTS;
            default:
                return BASE + SORT_TOP + WINDOW_DAY + pageIndex;
        }
    }

    static final String getUrlForEditor(String imageId) {
        return BASE + imageId;
    }

    public static ArrayList<String> getListString(int fragmentType, String key) {
        Context context = MemeGeneratorApplication.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(Integer.toString(fragmentType), Context.MODE_PRIVATE);
        return new ArrayList<>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }

    public static void putListString(int fragmentType, List<String> stringList, String key) {
        Context context = MemeGeneratorApplication.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(Integer.toString(fragmentType), Context.MODE_PRIVATE);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }
}
