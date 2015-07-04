package org.sebbas.android.memegenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import org.sebbas.android.memegenerator.dataloader.DataLoader;
import org.sebbas.android.memegenerator.fragments.ChartFragment;
import org.sebbas.android.memegenerator.fragments.ExploreFragment;
import org.sebbas.android.memegenerator.fragments.GalleryFragment;
import org.sebbas.android.memegenerator.fragments.GifFragment;
import org.sebbas.android.memegenerator.fragments.MemeFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static final String IMAGE_TINY = "s";
    public static final String IMAGE_SMALL = "t";
    public static final String IMAGE_MEDIUM = "m";
    public static final String IMAGE_LARGE = "l";

    private static final String MEMES = "https://api.imgur.com/3/album/dPXBy/images";
    private static final String GIFS = "https://api.imgur.com/3/album/zQK3s/images";

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

    public static String imageUrlToThumbnailUrl(String imageUrl, String id, String size) {
        return imageUrl.replaceAll(id, id + size);
    }

    public static final String getDataUrl(String fragmentTag) {
        String url = "";

        switch (fragmentTag) {
            case MemeFragment.TAG_CHILD_ONE:
            case MemeFragment.TAG_CHILD_TWO:
            case MemeFragment.TAG_CHILD_THREE:
                url = MEMES;
                break;
            case GifFragment.TAG_CHILD_ONE:
            case GifFragment.TAG_CHILD_TWO:
            case GifFragment.TAG_CHILD_THREE:
                url = GIFS;
                break;
            case ExploreFragment.TAG:
                break;
            case GalleryFragment.TAG_CHILD_ONE:
            case GalleryFragment.TAG_CHILD_TWO:
            case GalleryFragment.TAG_CHILD_THREE:
                url = GIFS;
                break;
            case ChartFragment.TAG:
                break;
        }
        return url;
    }

    public static int getLoadingLocation(String fragmentTag) {
        int location;

        switch (fragmentTag) {
            case MemeFragment.TAG_CHILD_ONE:
            case MemeFragment.TAG_CHILD_TWO:
            case MemeFragment.TAG_CHILD_THREE:
            case GifFragment.TAG_CHILD_ONE:
            case GifFragment.TAG_CHILD_TWO:
            case GifFragment.TAG_CHILD_THREE:
            case GalleryFragment.TAG_CHILD_ONE:
            case GalleryFragment.TAG_CHILD_TWO:
            case GalleryFragment.TAG_CHILD_THREE:
            case ExploreFragment.TAG:
                location = DataLoader.INTERNET;
                break;
            case ChartFragment.TAG:
                location = DataLoader.RESOURCE;
                break;
            default:
                location = DataLoader.INTERNET;
        }
        return location;
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

    public static char getTitleLetter(String title) {
        String firstLetter = title.substring(0, 1);
        if (firstLetter.matches("[a-zA-Z]")) {
            return firstLetter.toUpperCase().charAt(0);
        } else {
            return NUMBERS_HEADER_LETTER.charAt(0);
        }
    }

    public static String changeFileExtension(String url, String newExtension) {
        String urlWithoutExtension;
        if (url.contains(".")) {
            urlWithoutExtension = url.substring(0, url.lastIndexOf('.'));
            return urlWithoutExtension + newExtension;
        } else {
            // Could not change file extension
            return url;
        }
    }

    public static boolean stringPatternMatch(String sentence, String pattern) {
        return sentence.toLowerCase().contains(pattern.toLowerCase());
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
    * Taken from https://gist.github.com/dmsherazi/5985a093076a8c4e7c38 and adapted slightly
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

    public static ArrayList<String> getListString(Context context, String fragmentType, String key) {
        SharedPreferences preferences = context.getSharedPreferences(fragmentType, Context.MODE_PRIVATE);
        return new ArrayList<>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }

    public static void putListString(Context context, String fragmentType, List<String> stringList, String key) {
        SharedPreferences preferences = context.getSharedPreferences(fragmentType, Context.MODE_PRIVATE);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    public static ArrayList<Integer> getListInteger(Context context, String fragmentType, String key) {
        SharedPreferences preferences = context.getSharedPreferences(fragmentType, Context.MODE_PRIVATE);
        String[] strings = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        Integer[] ints = stringArrayToIntArray(strings);
        return new ArrayList<>(Arrays.asList(ints));
    }

    public static void putListInteger(Context context, String fragmentType, List<Integer> integerList, String key) {
        SharedPreferences preferences = context.getSharedPreferences(fragmentType, Context.MODE_PRIVATE);
        Integer[] myIntegerList = integerList.toArray(new Integer[integerList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myIntegerList)).apply();
    }

    private static Integer[] stringArrayToIntArray(String[] stringArray) {
        final Integer[] intArray = new Integer[stringArray.length];
        for (int i=0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i]);
        }
        return intArray;
    }
}
