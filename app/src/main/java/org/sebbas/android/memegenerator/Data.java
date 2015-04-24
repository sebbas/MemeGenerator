package org.sebbas.android.memegenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Data {

    static final String THUMBNAIL_SIZE = "m";

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
            case ViewPagerRecyclerViewFragment.VIRAL:
                System.out.println("Called viral");
                return BASE + SORT_VIRAL + pageIndex;
            case ViewPagerRecyclerViewFragment.TIME:
                System.out.println("Called new");
                return BASE + SORT_TIME + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_DAY:
                System.out.println("Called day");
                return BASE + SORT_TOP + WINDOW_DAY + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_WEEK:
                System.out.println("Called week");
                return BASE + SORT_TOP + WINDOW_WEEK + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_MONTH:
                System.out.println("Called month");
                return BASE + SORT_TOP + WINDOW_MONTH + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_YEAR:
                System.out.println("Called year");
                return BASE + SORT_TOP + WINDOW_YEAR + pageIndex;
            case ViewPagerRecyclerViewFragment.WINDOW_ALL:
                System.out.println("Called all");
                return BASE + SORT_TOP + WINDOW_ALL + pageIndex;
            case ViewPagerRecyclerViewFragment.DEFAULTS:
                return BASE_DEFAULTS;
            default:
                return BASE + SORT_TOP + WINDOW_DAY + pageIndex;
        }
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
