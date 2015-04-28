package org.sebbas.android.memegenerator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataLoader {

    private static final String TAG = "DataLoader";

    private List<String> mViewCounts;
    private List<String> mImageUrls;
    private List<String> mImageIds;
    private List<String> mImageTitles;
    private List<String> mTimeStamps;

    private volatile boolean mParsingComplete = false;
    private volatile boolean mParsingSuccessful = false;
    private boolean mConnectionUnavailable = false;


    private DataLoaderCallback mDataLoaderCallback;
    private int mFragmentType;

    public DataLoader(Fragment fragment, int fragmentType) {
        mFragmentType = fragmentType;
        mDataLoaderCallback = (DataLoaderCallback) fragment;

        // Restore array lists from previous session or if restore not possible (on startup) then get new lists
        mViewCounts = Data.getListString(fragmentType, "viewCounts");
        mImageUrls = Data.getListString(fragmentType, "imageUrls");
        mImageIds = Data.getListString(fragmentType, "imageIds");
        mImageTitles = Data.getListString(fragmentType, "imageTitles");
        mTimeStamps = Data.getListString(fragmentType, "timeStamps");
    }

    public void load(String url) {
        AsyncLoader asyncLoader = new AsyncLoader();
        asyncLoader.execute(url);
    }

    public String getViewCountAt(int position) {
        String count = "";
        if (mViewCounts != null && mViewCounts.size() > position) {
            count = mViewCounts.get(position);
        }
        return count;
    }

    public String getImageUrlAt(int position) {
        String url = "";
        if (mImageUrls != null && mImageUrls.size() > position) {
            url = mImageUrls.get(position);
        }
        return url;
    }

    public String getImageIdAt(int position) {
        String imageId = "";
        if (mImageIds != null && mImageIds.size() > position) {
            imageId = mImageIds.get(position);
        }
        return imageId;
    }

    public String getImageTitleAt(int position) {
        String imageTitle = "";
        if (mImageTitles != null && mImageTitles.size() > position) {
            imageTitle = mImageTitles.get(position);
        }
        return imageTitle;
    }

    public String getTimeStampAt(int position) {
        String timeStamp = "";
        if (mTimeStamps != null && mTimeStamps.size() > position) {
            timeStamp = mTimeStamps.get(position);
        }
        return timeStamp;
    }

    public int getItemCount() {
        int count = 0;
        if (mImageUrls != null) {
            count = mImageUrls.size();
        }
        return count;
    }

    private class AsyncLoader extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            if (Utils.isNetworkAvailable()) {
                mConnectionUnavailable = false;

                String url = params[0];

                fetchJSON(url);
                while (!mParsingComplete);

                if (mParsingSuccessful) {
                    // Save array lists for next session
                    Data.putListString(mFragmentType, mViewCounts, "viewCounts");
                    Data.putListString(mFragmentType, mImageUrls, "imageUrls");
                    Data.putListString(mFragmentType, mImageIds, "imageIds");
                    Data.putListString(mFragmentType, mImageTitles, "imageTitles");
                    Data.putListString(mFragmentType, mTimeStamps, "timeStamps");

                }
            } else {
                mConnectionUnavailable = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mConnectionUnavailable) {
                mDataLoaderCallback.onConnectionUnavailable();
            } else if (!mParsingSuccessful) {
                mDataLoaderCallback.onConnectionTimeout();
            } else {
                mDataLoaderCallback.onDataLoadSuccessful();
            }
        }
    }

    private void fetchJSON(String urlString) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
            okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout

            Request request = new Request.Builder()
                    .url(urlString)
                    .addHeader("Authorization", "Client-ID " + Utils.getImgurClientId())
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            InputStream stream = response.body().byteStream();
            String data = convertStreamToString(stream);

            readAndParseJSON(data);

        } catch (Exception e) {
            e.printStackTrace();
            mParsingComplete = true;
            mParsingSuccessful = false;
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @SuppressLint("NewApi")
    private void readAndParseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);
            //Log.d(TAG, in);

            // Only start loading json if success field is true
            if (isValid(reader)) {
                JSONArray data = reader.getJSONArray("data");
                parseJsonArray(data);
                mParsingSuccessful = true;
            } else {
                mParsingSuccessful = false;
            }
            mParsingComplete = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJsonArray(JSONArray data) throws JSONException {

        for (int i = 0; i < data.length(); i++) {
            JSONObject image = data.getJSONObject(i);

            // Only look at json objects that represent images
            if (!isAlbum(image) && !isAnimated(image)) {
                String views = Integer.toString(image.getInt("views"));
                String imageUrl = image.getString("link");
                String imageId = image.getString("id");
                String imageTitle = image.getString("title");
                String timeStamp = Integer.toString(image.getInt("datetime"));

                mViewCounts.add(views);
                mImageUrls.add(imageUrl);
                mImageIds.add(imageId);
                mImageTitles.add(imageTitle);
                mTimeStamps.add(timeStamp);

                /*if (!mViewCounts.contains(views)) {
                    mViewCounts.add(views);
                }

                if (!mImageUrls.contains(imageUrl)) {
                    mImageUrls.add(imageUrl);
                }

                if (!mImageIds.contains(imageId)) {
                    mImageIds.add(imageId);
                }

                if (!mImageTitles.contains(imageTitle)) {
                    mImageTitles.add(imageTitle);
                }

                if (!mTimeStamps.contains(timeStamp)) {
                    mTimeStamps.add(timeStamp);
                }*/
            }
        }
    }

    private boolean isAlbum(JSONObject image) throws JSONException {
        boolean isAlbum = false;
        try {
            isAlbum = image.getBoolean("is_album");
        } finally {
            return isAlbum;
        }
    }

    private boolean isAnimated(JSONObject image) throws JSONException {
        boolean isAnimated = false;
        try {
            isAnimated = image.getBoolean("animated");
        } finally {
            return isAnimated;
        }
    }

    private boolean isValid(JSONObject reader) throws JSONException {
        return reader.getBoolean("success");
    }

}
