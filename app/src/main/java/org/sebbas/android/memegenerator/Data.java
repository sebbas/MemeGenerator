package org.sebbas.android.memegenerator;

final class Data {
    private static final String BASE = "http://version1.api.memegenerator.net/";
    private static final String POPULAR = "Generators_Select_ByPopular";
    private static final String TRENDING = "Generators_Select_ByTrending";
    private static final String NEW = "Generators_Select_ByNew?pageIndex=0&pageSize=24";

    static final String URL_POPULAR = BASE + POPULAR;
    static final String URL_TRENDING = BASE + TRENDING;
    static final String URL_NEW = BASE + NEW;

    private Data() {
        // No instances.
    }
}