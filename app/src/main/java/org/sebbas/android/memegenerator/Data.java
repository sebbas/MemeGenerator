package org.sebbas.android.memegenerator;

final class Data {
    private static final String BASE = "http://version1.api.memegenerator.net/";
    private static final String TEMPLATE_POPULAR = "Generators_Select_ByPopular";
    private static final String TEMPLATE_TRENDING = "Generators_Select_ByTrending";
    private static final String TEMPLATE_NEW = "Generators_Select_ByNew?pageIndex=0&pageSize=24";

    static final String URL_POPULAR = BASE + TEMPLATE_POPULAR;
    static final String URL_TRENDING = BASE + TEMPLATE_TRENDING;
    static final String URL_NEW = BASE + TEMPLATE_NEW;

    private Data() {
        // No instances.
    }
}