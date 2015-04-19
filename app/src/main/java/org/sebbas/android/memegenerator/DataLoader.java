package org.sebbas.android.memegenerator;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private List<String> mImageUrls = new ArrayList<String>();
    private List<String> mDisplayNames = new ArrayList<String>();
    private DataLoaderCallback mDataLoaderCallback;

    public DataLoader(Fragment fragment) {
        mDataLoaderCallback = (DataLoaderCallback) fragment;
    }

    public void loadData(String url) {
        AsyncLoader asyncLoader = new AsyncLoader();
        asyncLoader.execute(url);
        System.out.println("Started async loading with url: " + url);
    }

    public String getImageUrlAt(int position) {
        String url = "";
        if (mImageUrls != null && mImageUrls.size() > position) {
            url = mImageUrls.get(position);
        }
        return url;
    }

    public String getDisplayNameAt(int position) {
        String name = "";
        if (mDisplayNames != null) {
            name = mDisplayNames.get(position);
        }
        return name;
    }

    public int getItemCount() {
        int count = 0;
        if (mImageUrls != null) {
            count = mDisplayNames.size();
        }
        return count;
    }

    public interface DataLoaderCallback {
        void onDataLoadComplete();
    }

    private class AsyncLoader extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            // Fetch the image data
            JSONHandler jsonHandler = new JSONHandler(url);
            jsonHandler.fetchJSON();

            System.out.println("Fetched JSON");
            while (jsonHandler.parsingComplete) ;


            mImageUrls = (jsonHandler.getImageUrls());
            mDisplayNames = (jsonHandler.getDisplayNames());

            //System.out.println("imageUrl are: ");
            for (String theurl : mImageUrls) {
                //System.out.println(theurl);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDataLoaderCallback.onDataLoadComplete();
        }
    }
}