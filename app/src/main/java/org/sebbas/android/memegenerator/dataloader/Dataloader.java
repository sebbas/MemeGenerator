package org.sebbas.android.memegenerator.dataloader;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.DataLoaderCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataLoader implements Filterable {

    private static final String TAG = "DataLoader";
    public static final int INTERNET = 0;
    public static final int LOCAL = 1;

    private Context mContext;
    private DataLoaderCallback mDataLoaderCallback;
    private String mFragmentType;
    private List<Integer> mExcludedLineItemPositions;

    private List<String> mViewCounts;
    private List<String> mImageUrls;
    private List<String> mImageIds;
    private List<String> mTitles;
    private List<String> mTimeStamps;
    private List<Integer> mImageWidths;
    private List<Integer> mImageHeights;

    public DataLoader(RecyclerFragment fragment) {
        mContext = fragment.getActivity();
        mDataLoaderCallback = fragment;
        mFragmentType = fragment.getFragmentType();
        mExcludedLineItemPositions = new ArrayList<>();

        // Restore array lists from previous session or if restore not possible (on startup)
        // then get new lists
        loadData();
    }

    public void load(String url, int location) {
        AsyncLoader asyncLoader = new AsyncLoader();
        asyncLoader.execute(url, location);
    }

    public void update(String localPath) {
        AsyncUpdater asyncUpdater = new AsyncUpdater();
        asyncUpdater.execute(localPath);
    }

    public void filter(String constraint) {
        this.getFilter().filter(constraint);
    }

    public ArrayList<LineItem> getSuperSlimLineItems() {
        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;
        ArrayList<LineItem> resultItems = new ArrayList<>();

        int tmp = 0;
        int offset = 2;
        for (int i = 0; i < getItemCount() + offset; i++) {
            if (i < offset) {
                // Insert new header view and update section data.
                sectionManager = (sectionManager + 1) % 2;
                sectionFirstPosition = tmp + headerCount;
                headerCount += 1;
                resultItems.add(LineItem.newSuperSlimHeaderInstance(
                        "x", false, sectionManager, sectionFirstPosition));
            } else {
                boolean isAllowedPosition = isAllowedPosition(i - offset);
                if (isAllowedPosition) {
                    String title = getTitleAt(i - offset);
                    String imageUrl = getImageUrlAt(i - offset);
                    Log.d(TAG, imageUrl);
                    String imageId = getImageIdAt(i - offset);
                    String viewCount = getViewCountAt(i - offset);
                    String timeStamp = getTimeStampAt(i - offset);
                    int imageWidth = getImageWidthAt(i - offset);
                    int imageHeight = getImageHeightAt(i - offset);
                    String header = String.valueOf(Utils.getTitleLetter(getTitleAt(i - offset)));

                    if (!TextUtils.equals(lastHeader, header)) {
                        // Insert new header view and update section data.
                        sectionManager = (sectionManager + 1) % 2;
                        sectionFirstPosition = tmp + headerCount;
                        lastHeader = header;
                        headerCount += 1;
                        resultItems.add(LineItem.newSuperSlimHeaderInstance(
                                header, true, sectionManager, sectionFirstPosition));
                    }
                    resultItems.add(LineItem.newSuperSlimInstance(
                            title, imageUrl, imageId, viewCount, timeStamp, imageWidth, imageHeight,
                            false, sectionManager, sectionFirstPosition));
                    tmp++;
                }
            }
        }
        return resultItems;
    }

    public ArrayList<LineItem> getLineItems() {
        ArrayList<LineItem> resultItems = new ArrayList<>();

        int offset = 2;
        for (int i = 0; i < getItemCount() + offset; i++) {
            if (i < offset) {
                resultItems.add(LineItem.newHeaderInstance(" "));
            } else {
                boolean isAllowedPosition = isAllowedPosition(i - offset);
                if (isAllowedPosition) {
                    String title = getTitleAt(i - offset);
                    String imageUrl = getImageUrlAt(i - offset);
                    String imageId = getImageIdAt(i - offset);
                    String viewCount = getViewCountAt(i - offset);
                    String timeStamp = getTimeStampAt(i - offset);
                    int imageWidth = getImageWidthAt(i - offset);
                    int imageHeight = getImageHeightAt(i - offset);

                    resultItems.add(LineItem.newInstance(
                            title, imageUrl, imageId, viewCount, timeStamp, imageWidth, imageHeight));
                }
            }
        }
        return resultItems;
    }

    private boolean isAllowedPosition(int i) {
        // List of excluded items is empty -> all items are allowed
        if (mExcludedLineItemPositions == null || mExcludedLineItemPositions.isEmpty()) {
            return true;
        }
        // If excluded items list contains position -> item not allowed!
        return !mExcludedLineItemPositions.contains(i);
    }

    private class AsyncUpdater extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String url = (String) params[0];

            // TODO
            return null;
        }
    }

    private class AsyncLoader extends AsyncTask<Object, Void, Integer> {

        private static final int CONNECTION_UNAVAILABLE = 0;
        private static final int CONNECTION_TIMEOUT = 1;
        private static final int CONNECTION_SUCCESS = 2;

        @Override
        protected Integer doInBackground(Object... params) {
            String url = (String) params[0];
            int loadingLocation = (Integer) params[1];

            if (!Utils.isNetworkAvailable(mContext)) {
                return CONNECTION_UNAVAILABLE;
            }

            boolean loadSuccess = false;
            // Load data depending on location
            switch(loadingLocation) {
                case INTERNET:
                    loadSuccess = loadFromInternet(url);
                    break;
                case LOCAL:
                    loadSuccess = loadFromStorage(url);
                    break;
                default:
                    loadSuccess = loadFromInternet(url);
            }

            if (loadSuccess) {
                saveData();
                return CONNECTION_SUCCESS;
            } else {
                return CONNECTION_TIMEOUT;
            }
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);

            switch (resultCode) {
                case CONNECTION_UNAVAILABLE:
                    mDataLoaderCallback.onConnectionUnavailable();
                    break;
                case CONNECTION_TIMEOUT:
                    mDataLoaderCallback.onConnectionTimeout();
                    break;
                case CONNECTION_SUCCESS:
                    mDataLoaderCallback.onDataLoadSuccessful();
                    break;
                default:
                    mDataLoaderCallback.onConnectionTimeout();
            }
        }
    }

    private void saveData() {
        Utils.putListString(mContext, mFragmentType, mViewCounts, "viewCounts");
        Utils.putListString(mContext, mFragmentType, mImageUrls, "imageUrls");
        Utils.putListString(mContext, mFragmentType, mImageIds, "imageIds");
        Utils.putListString(mContext, mFragmentType, mTitles, "imageTitles");
        Utils.putListString(mContext, mFragmentType, mTimeStamps, "timeStamps");
        Utils.putListInteger(mContext, mFragmentType, mImageWidths, "imageWidths");
        Utils.putListInteger(mContext, mFragmentType, mImageHeights, "imageHeights");
    }

    private void loadData() {
        mViewCounts = Utils.getListString(mContext, mFragmentType, "viewCounts");
        mImageUrls = Utils.getListString(mContext, mFragmentType, "imageUrls");
        mImageIds = Utils.getListString(mContext, mFragmentType, "imageIds");
        mTitles = Utils.getListString(mContext, mFragmentType, "imageTitles");
        mTimeStamps = Utils.getListString(mContext, mFragmentType, "timeStamps");
        mImageWidths = Utils.getListInteger(mContext, mFragmentType, "imageWidths");
        mImageHeights = Utils.getListInteger(mContext, mFragmentType, "imageHeights");
    }

    private boolean loadFromInternet(String url) {
        String json = getJson(url);
        if (json == null) {
            return false;
        }

        boolean parsingSuccess = parseJson(json);
        if (!parsingSuccess) {
            return false;
        }

        return true;
    }

    private boolean loadFromStorage(String url) {
        // TODO
        return true;
    }

    private String getJson(String urlString) {
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
            return convertStreamToString(stream);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception while fetching JSON");
            return null;
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private boolean parseJson(String in) {
        try {
            JSONObject reader = new JSONObject(in);

            // Only start loading json if success field in json string is true
            if (!isValid(reader)) {
                return false;
            }

            JSONArray data = reader.getJSONArray("data");

            mViewCounts.clear();
            mImageUrls.clear();
            mImageIds.clear();
            mTitles.clear();
            mTimeStamps.clear();
            mImageWidths.clear();
            mImageHeights.clear();

            for (int i = 0; i < data.length(); i++) {
                JSONObject image = data.getJSONObject(i);

                String views = Integer.toString(image.getInt("views"));
                String imageUrl = image.getString("link");
                String imageId = image.getString("id");
                String imageTitle = image.getString("title");
                String timeStamp = Integer.toString(image.getInt("datetime"));
                int imageWidth = image.getInt("width");
                int imageHeight = image.getInt("height");

                mViewCounts.add(views);
                mImageUrls.add(imageUrl);
                mImageIds.add(imageId);
                mTitles.add(imageTitle);
                mTimeStamps.add(timeStamp);
                mImageWidths.add(imageWidth);
                mImageHeights.add(imageHeight);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isValid(JSONObject reader) {
        try {
            return reader.getBoolean("success");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            private List<Integer> filterResultList;

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                this.filterResultList = getFilteredResults(charSequence);

                FilterResults results = new FilterResults();
                results.values = filterResultList;
                results.count = filterResultList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mExcludedLineItemPositions = (ArrayList<Integer>) filterResults.values;

                // Notify fragment that filtering has finished
                mDataLoaderCallback.onFilterComplete();
            }

            private List<Integer> getFilteredResults(CharSequence constraint) {
                ArrayList<Integer> excludedItems = new ArrayList<>();

                if (constraint.length() == 0) {
                    return excludedItems;
                }

                for (int i = 0; i < getItemCount(); i++) {

                    String currentLineItemTitle = getTitleAt(i);

                    // If title and pattern do not match -> exclude item
                    if (!Utils.stringPatternMatch(currentLineItemTitle, (String) constraint)) {
                        excludedItems.add(i);
                    }
                }
                return excludedItems;
            }
        };
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

    private int getImageWidthAt(int position) {
        int imageWidth = 0;
        if (mImageWidths != null && mImageWidths.size() > position) {
            imageWidth = mImageWidths.get(position);
        }
        return imageWidth;
    }

    private int getImageHeightAt(int position) {
        int imageHeight = 0;
        if (mImageHeights != null && mImageHeights.size() > position) {
            imageHeight = mImageHeights.get(position);
        }
        return imageHeight;
    }

    private int getItemCount() {
        int count = 0;
        if (mTitles != null) {
            count = mTitles.size();
        }
        return count;
    }
}
