package org.sebbas.android.memegenerator;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class LineItem implements Parcelable {

    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_IMAGE_ID = "imageId";
    private static final String KEY_VIEW_COUNT = "viewCount";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_IMAGE_WIDTH = "imageWidth";
    private static final String KEY_IMAGE_HEIGHT = "imageHeight";

    private int mSectionManager;
    private int mSectionFirstPosition;
    private boolean mIsHeader;
    private String mTitle;
    private String mImageUrl;
    private String mImageId;
    private String mViewCount;
    private String mTimeStamp;
    private int mImageWidth;
    private int mImageHeight;

    // Regular line items
    public static LineItem newInstance(String title, String imageUrl, String imageId,
                                       String viewCount, String timeStamp, int imageWidth,
                                       int imageHeight) {

        return new LineItem(title, imageUrl, imageId, viewCount, timeStamp, imageWidth, imageHeight);
    }

    public static LineItem newHeaderInstance(String title) {

        return new LineItem(title);
    }

    // SuperSlim line items
    public static LineItem newSuperSlimInstance(String title, String imageUrl, String imageId,
                                       String viewCount, String timeStamp, int imageWidth,
                                       int imageHeight, boolean isHeader,
                                       int sectionManager, int sectionFirstPosition) {

        return new LineItem(title, imageUrl, imageId, viewCount, timeStamp, imageWidth, imageHeight,
                isHeader, sectionManager, sectionFirstPosition);
    }

    public static LineItem newSuperSlimHeaderInstance(String title, boolean isHeader, int sectionManager,
                                         int sectionFirstPosition) {

        return new LineItem(title, isHeader, sectionManager, sectionFirstPosition);
    }

    // Constructor for header items
    private LineItem(String title) {
        mTitle = title;
    }

    // Constructor for items
    private LineItem(String title, String imageUrl, String imageId, String viewCount,
                     String timeStamp, int imageWidth, int imageHeight) {
        this(title);
        mImageUrl = imageUrl;
        mImageId = imageId;
        mViewCount = viewCount;
        mTimeStamp = timeStamp;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
    }

    // Constructor for superslim header items
    private LineItem(String title, boolean isHeader, int sectionManager,
                    int sectionFirstPosition) {
        this(title);
        mIsHeader = isHeader;
        mSectionManager = sectionManager;
        mSectionFirstPosition = sectionFirstPosition;
    }

    // Constructor for superslim items
    private LineItem(String title, String imageUrl, String imageId, String viewCount,
                    String timeStamp, int imageWidth, int imageHeight, boolean isHeader, int sectionManager,
                    int sectionFirstPosition) {
        this(title, isHeader, sectionManager, sectionFirstPosition);
        mImageUrl = imageUrl;
        mImageId = imageId;
        mViewCount = viewCount;
        mTimeStamp = timeStamp;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
    }

    public boolean isHeaderItem() {
        return mIsHeader;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public int getSectionFirstPosition() {
        return mSectionFirstPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageId() {
        return mImageId;
    }

    public String getViewCount() {
        return mViewCount;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public int getImageWidth() {
        return mImageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        // insert the key value pairs to the bundle
        bundle.putString(KEY_TITLE, getTitle());
        bundle.putString(KEY_IMAGE_URL, getImageUrl());
        bundle.putString(KEY_IMAGE_ID, getImageId());
        bundle.putString(KEY_VIEW_COUNT, getViewCount());
        bundle.putString(KEY_TIMESTAMP, getTimeStamp());
        bundle.putInt(KEY_IMAGE_WIDTH, getImageWidth());
        bundle.putInt(KEY_IMAGE_HEIGHT, getImageHeight());

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }
}
