package org.sebbas.android.memegenerator;

import android.content.Context;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class PicassoCache {

    /**
     * Static Picasso Instance
     */
    private static Picasso sPicassoInstance = null;

    /**
     * PicassoCache Constructor
     *
     * @param context application Context
     */
    private PicassoCache(Context context) {

        OkHttpDownloader downloader = new OkHttpDownloader(context, Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(downloader);

        sPicassoInstance = builder.build();
    }

    /**
     * Get Singleton Picasso Instance
     *
     * @param context application Context
     * @return Picasso instance
     */
    public static Picasso getPicassoInstance(Context context) {

        if (sPicassoInstance == null) {

            new PicassoCache(context);
            return sPicassoInstance;
        }

        return sPicassoInstance;
    }

} 