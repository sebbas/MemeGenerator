package org.sebbas.android.memegenerator;

public class LineItem {

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

    public static LineItem newInstance(String title, String imageUrl, String imageId,
                                       String viewCount, String timeStamp, int imageWidth,
                                       int imageHeight, boolean isHeader,
                                       int sectionManager, int sectionFirstPosition) {

        return new LineItem(title, imageUrl, imageId, viewCount, timeStamp, imageWidth, imageHeight,
                isHeader, sectionManager, sectionFirstPosition);
    }

    public static LineItem newHeaderInstance(String title, boolean isHeader, int sectionManager,
                                         int sectionFirstPosition) {

        return new LineItem(title, isHeader, sectionManager, sectionFirstPosition);
    }

    // Section header item constructor
    private LineItem(String title, boolean isHeader, int sectionManager,
                    int sectionFirstPosition) {
        mTitle = title;
        mIsHeader = isHeader;
        mSectionManager = sectionManager;
        mSectionFirstPosition = sectionFirstPosition;
    }

    // Regular item constructor
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

}
