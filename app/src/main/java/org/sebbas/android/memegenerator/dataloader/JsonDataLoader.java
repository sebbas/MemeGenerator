package org.sebbas.android.memegenerator.dataloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sebbas.android.memegenerator.interfaces.DataLoaderCallback;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.adapter.RecyclerFragmentAdapter;
import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JsonDataLoader extends Dataloader {

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

    public JsonDataLoader(Fragment fragment, int fragmentType) {
        mContext = fragment.getActivity();
        mFragmentType = fragmentType;
        mDataLoaderCallback = (DataLoaderCallback) fragment;

        // Restore array lists from previous session or if restore not possible (on startup) then get new lists
        mViewCounts = Utils.getListString(mContext, fragmentType, "viewCounts");
        mImageUrls = Utils.getListString(mContext, fragmentType, "imageUrls");
        mImageIds = Utils.getListString(mContext, fragmentType, "imageIds");
        mTitles = Utils.getListString(mContext, fragmentType, "imageTitles");
        mTimeStamps = Utils.getListString(mContext, fragmentType, "timeStamps");
    }

    public void load(String url) {
        AsyncLoader asyncLoader = new AsyncLoader();
        asyncLoader.execute(url);
    }

    private String getViewCountAt(int position) {
        String count = "";
        if (mViewCounts != null && mViewCounts.size() > position) {
            count = mViewCounts.get(position);
        }
        return count;
    }

    private String getImageUrlAt(int position) {
        String url = "";
        if (mImageUrls != null && mImageUrls.size() > position) {
            url = mImageUrls.get(position);
        }
        return url;
    }

    private String getImageIdAt(int position) {
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

    private String getTimeStampAt(int position) {
        String timeStamp = "";
        if (mTimeStamps != null && mTimeStamps.size() > position) {
            timeStamp = mTimeStamps.get(position);
        }
        return timeStamp;
    }

    public List<LineItem> getLineItems(List<Integer> allowedLineItemPositions, boolean superSlim) {
        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;
        ArrayList<LineItem> resultItems = new ArrayList<>();

        int tmp = 0;
        for (int i = 0; i < getItemCount() + 1; i++) {
            if (i == 0) {
                // Insert new header view and update section data.
                sectionManager = (sectionManager + 1) % 2;
                sectionFirstPosition = tmp + headerCount;
                headerCount += 1;
                resultItems.add(LineItem.newHeaderInstance(
                        "", true, sectionManager, sectionFirstPosition));
            } else {
                boolean isAllowedPosition = isAllowedPosition(i, allowedLineItemPositions);
                if (isAllowedPosition) {
                    String title = getTitleAt(i - 1);
                    String imageUrl = getImageUrlAt(i - 1);
                    String imageId = getImageIdAt(i - 1);
                    String viewCount = getViewCountAt(i - 1);
                    String timeStamp = getTimeStampAt(i - 1);
                    String header = Utils.getScrollHeaderTitleLetter(getTitleAt(i - 1));

                    if ((!TextUtils.equals(lastHeader, header) && superSlim)) {
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

    private class AsyncLoader extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {

            if (Utils.isNetworkAvailable(mContext)) {
                mConnectionUnavailable = false;
                String url = (String) params[0];

                // Load data depending on url
                if (url.equals(RecyclerFragmentAdapter.TOPICS_URL)) {
                    loadTopics();
                    mParsingComplete = true;
                    mParsingSuccessful = true;
                } else {
                    fetchJSON(url);
                }

                while (!mParsingComplete);

                if (mParsingSuccessful) {
                    // Save array lists for next session
                    Utils.putListString(mContext, mFragmentType, mViewCounts, "viewCounts");
                    Utils.putListString(mContext, mFragmentType, mImageUrls, "imageUrls");
                    Utils.putListString(mContext, mFragmentType, mImageIds, "imageIds");
                    Utils.putListString(mContext, mFragmentType, mTitles, "imageTitles");
                    Utils.putListString(mContext, mFragmentType, mTimeStamps, "timeStamps");
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
            Log.e(TAG, "Exception while fetching JSON");
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

    private void loadTopics() {
        String[] topics = mContext.getResources().getStringArray(R.array.main_topics);
        mTitles = Arrays.asList(topics);
    }
}
