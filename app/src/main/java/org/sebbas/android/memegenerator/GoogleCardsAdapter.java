package org.sebbas.android.memegenerator;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GoogleCardsAdapter extends BaseAdapter {

    private final Context mContext;
    private String mUrl;
    private List<String> mImageUrls = new ArrayList<String>();
    private List<String> mDisplayNames = new ArrayList<String>();
    private AdapterCallback mAdapterCallback;

    GoogleCardsAdapter(final Context context, Fragment fragment, String url) {
        mContext = context;
        mAdapterCallback = (AdapterCallback) fragment;
        mUrl = url;

        // Trigger async data loading
        triggerAsyncLoad();
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public String getItem(int position) {
        return mImageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_googlecards_card, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.activity_googlecards_card_textview);
            view.setTag(viewHolder);

            viewHolder.imageView = (ImageView) view.findViewById(R.id.activity_googlecards_card_imageview);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Set the text
        String displayName = mDisplayNames.get(position);
        viewHolder.textView.setText(displayName);

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext) //
                .load(url) //
                .placeholder(android.R.color.white) //
                .error(android.R.color.white) //
                .fit() //
                .centerCrop() //
                .tag(mContext) //
                .into(viewHolder.imageView);

        return view;
    }

    @SuppressWarnings({"PackageVisibleField", "InstanceVariableNamingConvention"})
    private static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    private class DataLoader extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAdapterCallback.onDataLoadStarted();
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            // Fetch the image data
            JSONHandler jsonHandler = new JSONHandler(url);
            jsonHandler.fetchJSON();

            while (jsonHandler.parsingComplete) ;

            mImageUrls = jsonHandler.getImageUrls();
            mDisplayNames = jsonHandler.getDisplayNames();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataSetChanged();
            mAdapterCallback.onDataLoadFinished();
        }

    }

    public void triggerAsyncLoad() {
        DataLoader dataLoader = new DataLoader();
        dataLoader.execute(mUrl);
    }

    public static interface AdapterCallback {
        void onDataLoadStarted();
        void onDataLoadFinished();
    }
}