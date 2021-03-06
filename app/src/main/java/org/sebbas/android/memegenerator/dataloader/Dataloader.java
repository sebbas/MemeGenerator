package org.sebbas.android.memegenerator.dataloader;

import android.content.Context;
import android.content.res.TypedArray;
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
import org.sebbas.android.memegenerator.R;
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
    public static final int RESOURCE = 1;

    private Context mContext;
    private DataLoaderCallback mDataLoaderCallback;
    private String mFragmentTag;
    private List<Integer> mExcludedLineItemPositions;

    private List<String> mViewCounts;
    private List<String> mImageUrls;
    private List<String> mImageIds;
    private List<String> mTitles;
    private List<String> mTimeStamps;
    private List<Integer> mImageWidths;
    private List<Integer> mImageHeights;

    private ArrayList<LineItem> mLineItems;

    public DataLoader(RecyclerFragment fragment) {
        mContext = fragment.getActivity();
        mDataLoaderCallback = fragment;
        mFragmentTag = fragment.getFragmentTag();
        mExcludedLineItemPositions = new ArrayList<>();

        mViewCounts = new ArrayList<>();
        mImageUrls = new ArrayList<>();
        mImageIds = new ArrayList<>();
        mTitles = new ArrayList<>();
        mTimeStamps = new ArrayList<>();
        mImageWidths = new ArrayList<>();
        mImageHeights = new ArrayList<>();
    }

    public void loadData(int location, String url, boolean isNetworkLoad) {
        AsyncLoader asyncLoader = new AsyncLoader();
        asyncLoader.execute(location, url, isNetworkLoad);
    }

    public void loadLineItems(int itemType) {
        LineItemFactory lineItemFactory = new LineItemFactory();
        lineItemFactory.execute(itemType);
    }

    public void update(String localPath) {
        AsyncUpdater asyncUpdater = new AsyncUpdater();
        asyncUpdater.execute(localPath);
    }

    public void filter(String constraint) {
        this.getFilter().filter(constraint);
    }

    private ArrayList<LineItem> generateSuperSlimLineItems() {
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
                resultItems.add(LineItem.newInstance(
                        "#", null, null, null, null, 0, 0, true, sectionManager,
                        sectionFirstPosition, headerCount));
            } else {
                boolean isAllowedPosition = isAllowedPosition(i - 1);
                if (isAllowedPosition) {
                    String title = getTitleAt(i - 1);
                    String imageUrl = getImageUrlAt(i - 1);
                    String imageId = getImageIdAt(i - 1);
                    String viewCount = getViewCountAt(i - 1);
                    String timeStamp = getTimeStampAt(i - 1);
                    int imageWidth = getImageWidthAt(i - 1);
                    int imageHeight = getImageHeightAt(i - 1);
                    String header = String.valueOf(Utils.getTitleLetter(getTitleAt(i - 1)));

                    if (!TextUtils.equals(lastHeader, header)) {
                        // Insert new header view and update section data.
                        sectionManager = (sectionManager + 1) % 2;
                        sectionFirstPosition = tmp + headerCount;
                        lastHeader = header;
                        headerCount += 1;
                        resultItems.add(LineItem.newInstance(
                                header, null, null, null, null, 0, 0, true, sectionManager,
                                sectionFirstPosition, headerCount));
                    }

                    resultItems.add(LineItem.newInstance(
                            title, imageUrl, imageId, viewCount, timeStamp, imageWidth, imageHeight,
                            false, sectionManager, sectionFirstPosition, headerCount));
                    tmp++;
                }
            }
        }
        return resultItems;
    }

    private ArrayList<LineItem> generateLineItems() {
        ArrayList<LineItem> resultItems = new ArrayList<>();

        int headerCount = 1;
        for (int i = 0; i < getItemCount() + 1; i++) {
            if (i == 0) {
                resultItems.add(LineItem.newInstance(
                        "#", null, null, null, null, 0, 0, true, 0, 0, headerCount));
            } else {
                boolean isAllowedPosition = isAllowedPosition(i - 1);
                if (isAllowedPosition) {
                    String title = getTitleAt(i - 1);
                    String imageUrl = getImageUrlAt(i - 1);
                    String imageId = getImageIdAt(i - 1);
                    String viewCount = getViewCountAt(i - 1);
                    String timeStamp = getTimeStampAt(i - 1);
                    int imageWidth = getImageWidthAt(i - 1);
                    int imageHeight = getImageHeightAt(i - 1);

                    resultItems.add(LineItem.newInstance(
                            title, imageUrl, imageId, viewCount, timeStamp, imageWidth, imageHeight,
                            false, 0, 0, headerCount));
                }
            }
        }
        return resultItems;
    }

    private ArrayList<LineItem> generateStaticLineItems() {
        ArrayList<LineItem> resultItems = new ArrayList<>();

        String[] lineItemTitles = mContext.getResources().getStringArray(R.array.more_line_item_titles);
        TypedArray lineItemIcons = mContext.getResources().obtainTypedArray(R.array.more_line_item_icons);
        clearLists();

        for (int i = 0; i < lineItemTitles.length; i++) {
            mImageUrls.add(Integer.toString(lineItemIcons.getResourceId(i, -1)));
            mTitles.add(lineItemTitles[i]);

            resultItems.add(LineItem.newInstance(getTitleAt(i), getImageUrlAt(i), null, null, null, 0, 0, false, 0, 0, 0));
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

    private class LineItemFactory extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int itemType = params[0];

            // Create line items depending on item type
            switch (itemType) {
                case RecyclerFragment.SUPER_SLIM:
                    mLineItems = generateSuperSlimLineItems();
                    break;
                case RecyclerFragment.CARD:
                    mLineItems = generateLineItems();
                    break;
                case RecyclerFragment.EXPLORE:
                    mLineItems = generateLineItems();
                    break;
                case RecyclerFragment.EXPLORE_DETAIL:
                    mLineItems = generateLineItems();
                    break;
                case RecyclerFragment.SIMPLE:
                    mLineItems = generateStaticLineItems();
                default:
                    mLineItems = generateLineItems();
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDataLoaderCallback.onLineItemsComplete();
        }
    }

    private class AsyncLoader extends AsyncTask<Object, Void, Integer> {

        private static final int CONNECTION_UNAVAILABLE = 0;
        private static final int LOAD_ERROR = 1;
        private static final int LOAD_SUCCESS = 2;

        @Override
        protected Integer doInBackground(Object... params) {
            int loadingLocation = (Integer) params[0];
            String url = (String) params[1];
            boolean isNetworkLoad = (boolean) params[2];

            if (!Utils.isNetworkAvailable(mContext)) {
                restoreData();
                return CONNECTION_UNAVAILABLE;
            }

            if (!isNetworkLoad) {
                restoreData();
                return LOAD_SUCCESS;
            }

            // Load data depending on location
            boolean loadSuccess = false;
            switch(loadingLocation) {
                case INTERNET:
                    loadSuccess = loadFromInternet(url);
                    break;
                case RESOURCE:
                    loadSuccess = loadFromStorage(url);
                    break;
            }

            if (loadSuccess) {
                saveData();
                return LOAD_SUCCESS;
            } else {
                restoreData();
                return LOAD_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);

            switch (resultCode) {
                case CONNECTION_UNAVAILABLE:
                    mDataLoaderCallback.onConnectionUnavailable();
                    break;
                case LOAD_ERROR:
                    mDataLoaderCallback.onDataLoadError();
                    break;
                case LOAD_SUCCESS:
                    mDataLoaderCallback.onDataLoadSuccess();
                    break;
                default:
                    mDataLoaderCallback.onDataLoadError();
            }
        }
    }

    private void saveData() {
        Utils.putListString(mContext, mFragmentTag, mViewCounts, "viewCounts");
        Utils.putListString(mContext, mFragmentTag, mImageUrls, "imageUrls");
        Utils.putListString(mContext, mFragmentTag, mImageIds, "imageIds");
        Utils.putListString(mContext, mFragmentTag, mTitles, "imageTitles");
        Utils.putListString(mContext, mFragmentTag, mTimeStamps, "timeStamps");
        Utils.putListInteger(mContext, mFragmentTag, mImageWidths, "imageWidths");
        Utils.putListInteger(mContext, mFragmentTag, mImageHeights, "imageHeights");
    }

    private void restoreData() {
        mViewCounts = Utils.getListString(mContext, mFragmentTag, "viewCounts");
        mImageUrls = Utils.getListString(mContext, mFragmentTag, "imageUrls");
        mImageIds = Utils.getListString(mContext, mFragmentTag, "imageIds");
        mTitles = Utils.getListString(mContext, mFragmentTag, "imageTitles");
        mTimeStamps = Utils.getListString(mContext, mFragmentTag, "timeStamps");
        mImageWidths = Utils.getListInteger(mContext, mFragmentTag, "imageWidths");
        mImageHeights = Utils.getListInteger(mContext, mFragmentTag, "imageHeights");
    }

    private boolean loadFromInternet(String url) {
        String json = getJson(url);
        if (json == null) {
            return false;
        }

        boolean parsingSuccess = parseJson(json);
        if (parsingSuccess) {
            return true;
        }
        return false;

    }

    private boolean loadFromStorage(String url) {
        /*String[] lineItemTitles = mContext.getResources().getStringArray(resourceOne);
        TypedArray lineItemIcons = mContext.getResources().obtainTypedArray(resourceTwo);
        clearLists();

        for (int i = 0; i < lineItemTitles.length; i++) {
            mImageUrls.add(Integer.toString(lineItemIcons.getResourceId(i, -1)));
            mTitles.add(lineItemTitles[i]);
        }*/
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

            clearLists();

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

    private void clearLists() {
        mViewCounts.clear();
        mImageUrls.clear();
        mImageIds.clear();
        mTitles.clear();
        mTimeStamps.clear();
        mImageWidths.clear();
        mImageHeights.clear();
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

    public ArrayList<LineItem> getLineItems() {
        return mLineItems;
    }
}
