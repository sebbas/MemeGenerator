package org.sebbas.android.memegenerator;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.github.mrengineer13.snackbar.SnackBar;

import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private List<String> mImageUrls = new ArrayList<>();
    private List<String> mDisplayNames = new ArrayList<>();
    private DataLoaderCallback mDataLoaderCallback;
    private int mFragmentType;
    private Fragment mFragment;

    public DataLoader(Fragment fragment, int fragmentType) {
        mFragmentType = fragmentType;
        mFragment = fragment;
        mDataLoaderCallback = (DataLoaderCallback) fragment;
        mImageUrls = Data.loadSettings(fragmentType);
    }

    public void loadData(String url) {
        AsyncLoader asyncLoader = new AsyncLoader();
        asyncLoader.execute(url);
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
        /*if (mDisplayNames != null) {
            name = mDisplayNames.get(position);
        }*/
        return name;
    }

    public int getItemCount() {
        int count = 0;
        if (mImageUrls != null) {
            count = mImageUrls.size();
        }
        return count;
    }

    private class AsyncLoader extends AsyncTask<String, Void, Void> {

        private boolean mParsingSuccessful = false;
        private boolean mConnectionUnavailable = false;

        @Override
        protected Void doInBackground(String... params) {

            if (Utils.isNetworkAvailable()) {
                String url = params[0];

                // Fetch the image data
                JSONHandler jsonHandler = new JSONHandler(url);
                jsonHandler.fetchJSON();

                while (!jsonHandler.mParsingComplete) ;

                // If parsing was successful, update array lists
                if (jsonHandler.mParsingSuccessful) {

                    mParsingSuccessful = jsonHandler.mParsingSuccessful;

                    mImageUrls = (jsonHandler.getImageUrls());
                    mDisplayNames = (jsonHandler.getDisplayNames());

                    Data.saveSettings(mFragmentType, mImageUrls);
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

}
