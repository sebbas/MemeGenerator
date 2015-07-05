package org.sebbas.android.memegenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TopicsDetailGridAdapter extends BaseAdapter {
    private Context mContext;
    private final ArrayList<LineItem> mLineItems;

    public TopicsDetailGridAdapter(Context context, ArrayList<LineItem> lineItems) {
        mContext = context;
        mLineItems = lineItems;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = inflater.inflate(R.layout.squared_item, null);

            ImageView imageView = (ImageView) gridView.findViewById(R.id.item_image);

            LineItem item = mLineItems.get(position);
            Glide.with(mContext)
                    .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_MEDIUM))
                    .asBitmap()
                    .centerCrop()
                    .into(imageView);

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return mLineItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}