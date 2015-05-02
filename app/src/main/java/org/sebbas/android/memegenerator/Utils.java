package org.sebbas.android.memegenerator;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;

public class Utils {

    public static final int REFRESH_ICON_TIME_SHOWN = 3000;
    private static final String NUMBERS_HEADER_LETTER = "#";

    private Utils() {
        // No instances
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getThumbnailUrl(String imageUrl, String id, String size) {
        return imageUrl.replaceAll(id, id + size);
    }

    /*
     * Returns your personal client id for imgur. The file with my own client id was
     * omitted on purpose for security reasons.
     *
     * For this to work for you, you have to add your personal imgur client id to the
     * string resources in this project.
     *
     * You can do so by creating a new file "imgur.xml" under /res/values which could
     * contain the following:
     *
     * <?xml version="1.0" encoding="utf-8"?><resources><item name="client_id" type="string">your_client_id</item></resources>
     *
     * Remember to replace 'your_client_id' with your actual imgur client id :)
     *
     * If you plan to share (e.g on Github) your fork of this project, you should consider
     * placing the file "imgur.xml" in your '.gitignore'. That way your client id will not
     * be compromised.
     */
    public static String getImgurClientId(Context context) {
        return context.getResources().getString(R.string.client_id);
    }

    public static String getScrollHeaderTitleLetter(String title) {
        String firstLetter = title.substring(0, 1);
        if (firstLetter.matches("[a-zA-Z]")) {
            return firstLetter;
        } else {
            return NUMBERS_HEADER_LETTER;
        }
    }

    public static boolean stringPatternMatch(String sentence, String pattern) {
        return sentence.toLowerCase().contains(pattern);
    }
}
