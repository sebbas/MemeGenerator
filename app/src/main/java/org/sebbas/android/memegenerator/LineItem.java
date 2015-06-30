package org.sebbas.android.memegenerator;

import android.os.Parcel;
import android.os.Parcelable;

public class LineItem implements Parcelable {

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
    private int mHeaderCount;

    public static LineItem newInstance(String title,
                                       String imageUrl,
                                       String imageId,
                                       String viewCount,
                                       String timeStamp,
                                       int imageWidth,
                                       int imageHeight,
                                       boolean isHeader,
                                       int sectionManager,
                                       int sectionFirstPosition,
                                       int headerCount) {

        return new LineItem(title, imageUrl, imageId, viewCount, timeStamp, imageWidth, imageHeight,
                isHeader, sectionManager, sectionFirstPosition, headerCount);
    }

    private LineItem(String title,
                     String imageUrl,
                     String imageId,
                     String viewCount,
                     String timeStamp,
                     int imageWidth,
                     int imageHeight,
                     boolean isHeader,
                     int sectionManager,
                     int sectionFirstPosition,
                     int headerCount) {

        mTitle = title;
        mImageUrl = imageUrl;
        mImageId = imageId;
        mIsHeader = isHeader;
        mViewCount = viewCount;
        mTimeStamp = timeStamp;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        mSectionManager = sectionManager;
        mSectionFirstPosition = sectionFirstPosition;
        mHeaderCount = headerCount;

    }
        protected LineItem(Parcel in) {
            mSectionManager = in.readInt();
            mSectionFirstPosition = in.readInt();
            mIsHeader = in.readByte() != 0x00;
            mTitle = in.readString();
            mImageUrl = in.readString();
            mImageId = in.readString();
            mViewCount = in.readString();
            mTimeStamp = in.readString();
            mImageWidth = in.readInt();
            mImageHeight = in.readInt();
            mHeaderCount = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mSectionManager);
            dest.writeInt(mSectionFirstPosition);
            dest.writeByte((byte) (mIsHeader ? 0x01 : 0x00));
            dest.writeString(mTitle);
            dest.writeString(mImageUrl);
            dest.writeString(mImageId);
            dest.writeString(mViewCount);
            dest.writeString(mTimeStamp);
            dest.writeInt(mImageWidth);
            dest.writeInt(mImageHeight);
            dest.writeInt(mHeaderCount);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<LineItem> CREATOR = new Parcelable.Creator<LineItem>() {
            @Override
            public LineItem createFromParcel(Parcel in) {
                return new LineItem(in);
            }

            @Override
            public LineItem[] newArray(int size) {
                return new LineItem[size];
            }
        };

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

    public int getHeaderCount() {
        return mHeaderCount;
    }

}
