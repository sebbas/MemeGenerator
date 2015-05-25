package org.sebbas.android.memegenerator.dataloader;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.ExploreFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalDataLoader extends Dataloader {

    private static final String TAG = "LocalDataLoader";
    private Context mContext;
    private RecyclerFragment mFragment;
    private int mPosition;

    private List<String> mTitles;
    private List<String> mImageIds;
    private List<String> mImageUrls;

    public LocalDataLoader(RecyclerFragment fragment, int position) {
        mContext = fragment.getActivity();
        mFragment = fragment;
        mPosition = position;
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

    private String getTitleAt(int position) {
        String imageTitle = "";
        if (mTitles != null && mTitles.size() > position) {
            imageTitle = mTitles.get(position);
        }
        return imageTitle;
    }

    private int getItemCount() {
        int count = 0;
        if (mTitles != null) {
            count = mTitles.size();
        }
        return count;
    }

    private void loadSubTopics(int position) {
        Resources resources = mContext.getResources();

        // Get typed array that contains subtopics array ids
        TypedArray subTopicTitles = resources.obtainTypedArray(R.array.sub_topics);
        TypedArray subTopicImageIds = resources.obtainTypedArray(R.array.sub_topics_urls);

        // Get array id at position
        int resIdTitles = subTopicTitles.getResourceId(position, 0);
        int resIdImageId = subTopicImageIds.getResourceId(position, 0);

        // Important: Recycle typedarray
        subTopicTitles.recycle();
        subTopicImageIds.recycle();

        mTitles = Arrays.asList(resources.getStringArray(resIdTitles));
        mImageIds = Arrays.asList(resources.getStringArray(resIdImageId));

        // Setup image urls from image ids
        ArrayList<String> imageUrls = new ArrayList<>();
        for (String imageId : mImageIds) {
            imageUrls.add(Utils.getBaseImgurImageUrl(imageId));
        }
        mImageUrls = imageUrls;
    }

    public List<LineItem> getLineItems() {
        List<LineItem> lineItems = new ArrayList<>();

        if (mFragment instanceof ExploreFragment) {
            loadSubTopics(mPosition);
        }
        // TODO Implement other possible local data load cases here

        for (int i = 0; i < getItemCount(); i++) {

            String title = getTitleAt(i);
            String imageUrl = getImageUrlAt(i);
            String imageId = getImageIdAt(i);

            LineItem newItem = LineItem.newInstance(
                    title, imageUrl, imageId, null, null, false, 0, 0);
            lineItems.add(newItem);
        }
        return lineItems;
    }

}
