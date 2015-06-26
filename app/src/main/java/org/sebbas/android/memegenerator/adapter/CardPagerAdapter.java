package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;

import java.util.ArrayList;

public class CardPagerAdapter extends PagerAdapter {

    private static final String TAG = "CardPagerAdapter";

    private ArrayList<LineItem> mLineItems;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CardPagerAdapter(Context context, ArrayList<LineItem> lineItems) {
        mContext = context;
        mLineItems = lineItems;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.card_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image_card);

        LineItem item = mLineItems.get(position);

        String formattedImageUrl = Utils.imageUrlToThumbnailUrl(item.getImageUrl(),
                item.getImageId(), Utils.IMAGE_MEDIUM);

        Glide.with(mContext)
                .load(item.getImageUrl())
                .override(item.getImageWidth(), item.getImageHeight())
                .centerCrop()
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return mLineItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==  object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
