package org.sebbas.android.memegenerator;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class CardPagerAdapter extends PagerAdapter {

    private static final String TAG = "CardPagerAdapter";

    private ArrayList<String> mImageUrls;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CardPagerAdapter(Context context, ArrayList<String> imageUrls) {
        mContext = context;
        mImageUrls = imageUrls;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.card_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image);

        String imageUrl = mImageUrls.get(position);
        Ion.with(imageView).load(imageUrl);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
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
