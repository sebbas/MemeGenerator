package org.sebbas.android.memegenerator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
