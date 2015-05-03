package org.sebbas.android.memegenerator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static final int REFRESH_ICON_TIME_SHOWN = 3000;
    public static final short NO_CONNECTION_HINT_TIME = 4000;
    public static final short TIMEOUT_HINT_TIME = 4000;
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

    public static String[] resourceArrayToStringArray(Context context, int[] intArray) {
        String[] stringArray = new String[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            int titleResource = intArray[i];
            stringArray[i] = context.getResources().getString(titleResource);
        }
        return stringArray;
    }

   /*
    * Taken from https://gist.github.com/dmsherazi/5985a093076a8c4e7c38 and slightly adapted
    */
    public static String getTimeAgoString(Context context, long timeStamp) {

        long timeDifference;
        long unixTime = System.currentTimeMillis() / 1000L;  //get current time in seconds.
        int j;

        // Get string resources
        String tense = context.getResources().getString(R.string.ago);

        double[] lengths = {60, 60, 24, 7, 4.35, 12, 10};
        timeDifference = unixTime - timeStamp;
        for (j = 0; timeDifference >= lengths[j] && j < lengths.length - 1; j++) {
            timeDifference /= lengths[j];
        }

        if (timeDifference == 1) {
            String[] periodsSingular = context.getResources().getStringArray(R.array.time_formats_singular);
            return timeDifference + " " + periodsSingular[j] + " " + tense;
        } else {
            String[] periodsPlural = context.getResources().getStringArray(R.array.time_formats_plural);
            return timeDifference + " " + periodsPlural[j] + " " + tense;
        }
    }

    public static String getViewCountString(Context context, String viewCount) {
        int count = Integer.valueOf(viewCount);

        if (count == 1) {
            String viewSingular = context.getResources().getString(R.string.view_singular);
            return count + " " + viewSingular;
        } else {
            String viewPlural = context.getResources().getString(R.string.view_plural);
            return count + " " + viewPlural;
        }
    }
}
