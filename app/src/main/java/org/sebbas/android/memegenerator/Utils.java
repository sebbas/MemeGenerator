package org.sebbas.android.memegenerator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private Utils() {
        // No instances
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MemeGeneratorApplication.getAppContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getThumbnailUrl(String imageUrl, String id, String size) {
        return imageUrl.replaceAll(id, id + size);
    }

    /*
     * Returns your personal client id for imgur. The file with my client id was
     * omitted on purpose for security reasons.
     *
     * For this to work for you, you have to add your personal imgur client id to the
     * string resources in this project.
     *
     * You can do so by creating a new file "imgur.xml" under /res/values containing the following:
     *
     * <?xml version="1.0" encoding="utf-8"?><resources><item name="client_id" type="string">your_client_id</item></resources>
     *
     * If you plan to share (e.g on Github) your fork of this project, you should consider
     * placing the file "imgur.xml" in your '.gitignore'. That way your client id will not
     * be compromised.
     */
    public static String getImgurClientId() {
        return MemeGeneratorApplication.
                getAppContext().getResources().getString(R.string.client_id);
    }

    public static List<String> toStringList(List<Integer> integerList) {
        List<String> stringList = new ArrayList<String>(integerList.size());
        for (Integer myInt : integerList) {
            stringList.add(String.valueOf(myInt));
        }
        return stringList;
    }

    public static List<Integer> fromStringList(List<String> stringList) {
        List<Integer> integerList = new ArrayList<Integer>(stringList.size());
        for (String string : stringList) {
            integerList.add(Integer.valueOf(string));
        }
        return integerList;
    }
}
