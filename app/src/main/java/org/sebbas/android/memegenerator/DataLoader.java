package org.sebbas.android.memegenerator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataLoader {

    private static final String TAG = "DataLoader";

    private List<String> mViewCounts;
    private List<String> mImageUrls;
    private List<String> mImageIds;
    private List<String> mTitles;
    private List<String> mTimeStamps;

    private volatile boolean mParsingComplete = false;
    private volatile boolean mParsingSuccessful = false;
    private boolean mConnectionUnavailable = false;
    private Context mContext;


    private DataLoaderCallback mDataLoaderCallback;
    private int mFragmentType;

    public DataLoader(Fragment fragment, int fragmentType) {
        mContext = fragment.getActivity();
        mFragmentType = fragmentType;
        mDataLoaderCallback = (DataLoaderCallback) fragment;

        // Restore array lists from previous session or if restore not possible (on startup) then get new lists
        mViewCounts = Data.getListString(mContext, fragmentType, "viewCounts");
        mImageUrls = Data.getListString(mContext, fragmentType, "imageUrls");
        mImageIds = Data.getListString(mContext, fragmentType, "imageIds");
        mTitles = Data.getListString(mContext, fragmentType, "imageTitles");
        mTimeStamps = Data.getListString(mContext, fragmentType, "timeStamps");
    }

    public void load(String url) {
        AsyncLoader asyncLoader = new AsyncLoader();
        asyncLoader.execute(url);
    }

    private void loadTopics() {
        String[] topics = mContext.getResources().getStringArray(R.array.main_topics);
        mTitles = Arrays.asList(topics);
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

    public String getTitleAt(int position) {
        String imageTitle = "";
        if (mTitles != null && mTitles.size() > position) {
            imageTitle = mTitles.get(position);
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

    public String[] getSubTopicTitles(int position) {
        Resources resources = mContext.getResources();

        // Get typed array that contains subtopics array ids
        TypedArray subTopics = resources.obtainTypedArray(R.array.sub_topics);

        // Get array id at position
        int resId = subTopics.getResourceId(position, 0);
        subTopics.recycle();

        return resources.getStringArray(resId);
    }

    public String[] getSubTopicImageUrls(int position) {
        Resources resources = mContext.getResources();

        // Get typed array that contains subtopics array ids
        TypedArray subTopicUrls = resources.obtainTypedArray(R.array.sub_topics_urls);

        // Get array id at position
        int resId = subTopicUrls.getResourceId(position, 0);
        subTopicUrls.recycle();

        return resources.getStringArray(resId);
    }

    public int getTopicsCount() {
        return mContext.getResources().obtainTypedArray(R.array.sub_topics).length();
    }

    public List<LineItem> getLineItems(List<Integer> allowedLineItemPositions, int layoutMode) {
        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;
        ArrayList<LineItem> resultItems = new ArrayList<>();

        int tmp = 0;
        for (int i = 0; i < getItemCount(); i++) {

            boolean isAllowedPosition = isAllowedPosition(i, allowedLineItemPositions);

            if (isAllowedPosition) {
                String title = getTitleAt(i);
                String imageUrl = getImageUrlAt(i);
                String imageId = getImageIdAt(i);
                String viewCount = getViewCountAt(i);
                String timeStamp = getTimeStampAt(i);
                String header = Utils.getScrollHeaderTitleLetter(getTitleAt(i));

                if (!TextUtils.equals(lastHeader, header) && layoutMode == UIOptions.LIST_LAYOUT) {
                    // Insert new header view and update section data.
                    sectionManager = (sectionManager + 1) % 2;
                    sectionFirstPosition = tmp + headerCount;
                    lastHeader = header;
                    headerCount += 1;
                    resultItems.add(LineItem.newHeaderInstance(
                            header, true, sectionManager, sectionFirstPosition));
                }
                resultItems.add(LineItem.newInstance(
                        title, imageUrl, imageId, viewCount, timeStamp, false,
                        sectionManager, sectionFirstPosition));
                tmp++;
            }
        }
        return resultItems;
    }

    public int getItemCount() {
        int count = 0;
        if (mTitles != null) {
            count = mTitles.size();
        }
        return count;
    }

    private boolean isAllowedPosition(int i, List<Integer> excludedLineItemPositions) {
        // If the are no excluded line items then all items are allowed -> return true
        if (excludedLineItemPositions == null || excludedLineItemPositions.isEmpty()) {
            return true;
        }
        // If excluded line item position matches i, then this item is not allowed -> return false
        return !excludedLineItemPositions.contains(i);
    }

    private class AsyncLoader extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            if (Utils.isNetworkAvailable(mContext)) {
                mConnectionUnavailable = false;

                String url = params[0];

                if (url != null) {
                    fetchJSON(url);
                } else {
                    loadTopics();
                    mParsingComplete = true;
                    mParsingSuccessful = true;
                }

                while (!mParsingComplete);

                if (mParsingSuccessful) {
                    // Save array lists for next session
                    Data.putListString(mContext, mFragmentType, mViewCounts, "viewCounts");
                    Data.putListString(mContext, mFragmentType, mImageUrls, "imageUrls");
                    Data.putListString(mContext, mFragmentType, mImageIds, "imageIds");
                    Data.putListString(mContext, mFragmentType, mTitles, "imageTitles");
                    Data.putListString(mContext, mFragmentType, mTimeStamps, "timeStamps");
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
                    .addHeader("Authorization", "Client-ID " + Utils.getImgurClientId(mContext))
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

        mViewCounts.clear();
        mImageUrls.clear();
        mImageIds.clear();
        mTitles.clear();
        mTimeStamps.clear();

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
                mTitles.add(imageTitle);
                mTimeStamps.add(timeStamp);


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
