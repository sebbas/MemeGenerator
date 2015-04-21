package org.sebbas.android.memegenerator;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Data {
    private static final String BASE = "http://version1.api.memegenerator.net/";

    // Generators
    private static final String TEMPLATE_TRENDING = "Generators_Select_ByTrending?";
    private static final String TEMPLATE_POPULAR = "Generators_Select_ByPopular?";
    private static final String TEMPLATE_NEW = "Generators_Select_ByNew?";
    private static final String TEMPLATE_SEARCH = "Generators_Search?q=";

    // Instances
    private static final String INSTANCE_POPULAR = "Instances_Select_ByPopular?";
    private static final String INSTANCE_NEW = "Instances_Select_ByNew?pageIndex=0&pageSize=24";

    private static final String AMPERSAND = "&";
    private static final String PAGE_INDEX = "pageIndex=";
    private static final String PAGE_SIZE = "pageSize=";

    private static final int PAGE_SIZE_DEFAULT = 24;

    private Data() {
        // No instances.
    }

    static final String getUrlForQuery(int pageIndex, String query) {
        return BASE + TEMPLATE_SEARCH + query.replace(" ", "&") + AMPERSAND + PAGE_INDEX + pageIndex + AMPERSAND + PAGE_SIZE + PAGE_SIZE_DEFAULT;
    }

    static final String getUrlForData(int pageIndex, int urlType) {
        switch (urlType) {
            case ViewPagerRecyclerViewFragment.TEMPLATE_TRENDING_TYPE:
                return BASE + TEMPLATE_TRENDING + PAGE_INDEX + pageIndex + AMPERSAND + PAGE_SIZE + PAGE_SIZE_DEFAULT;
            case ViewPagerRecyclerViewFragment.TEMPLATE_POPULAR_TYPE:
                return BASE + TEMPLATE_POPULAR + PAGE_INDEX + pageIndex + AMPERSAND + PAGE_SIZE + PAGE_SIZE_DEFAULT;
            case ViewPagerRecyclerViewFragment.TEMPLATE_NEW_TYPE:
                return BASE + TEMPLATE_NEW + PAGE_INDEX + pageIndex + AMPERSAND + PAGE_SIZE + PAGE_SIZE_DEFAULT;
            case ViewPagerRecyclerViewFragment.TEMPLATE_RANDOM_TYPE:
                return "";
            case ViewPagerRecyclerViewFragment.INSTANCE_POPULAR_TYPE:
                return BASE + INSTANCE_POPULAR + PAGE_INDEX + pageIndex + AMPERSAND + PAGE_SIZE + PAGE_SIZE_DEFAULT;
            case ViewPagerRecyclerViewFragment.INSTANCE_NEW_TYPE:
                return BASE + INSTANCE_NEW + PAGE_INDEX + pageIndex + AMPERSAND + PAGE_SIZE + PAGE_SIZE_DEFAULT;
            default:
                return BASE + TEMPLATE_TRENDING + PAGE_INDEX + pageIndex + AMPERSAND + PAGE_SIZE + PAGE_SIZE_DEFAULT;
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