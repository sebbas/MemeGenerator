package org.sebbas.android.memegenerator;

import android.content.Context;
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
    private DataLoader mDataLoader;

    GoogleCardsAdapter(final Context context, DataLoader dataLoader) {
        mContext = context;
        mDataLoader = dataLoader;
    }

    @Override
    public int getCount() {
        return mDataLoader.getItemCount();
    }

    @Override
    public String getItem(int position) {
        return mDataLoader.getImageUrlAt(position);
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

        String imageUrl = mDataLoader.getImageUrlAt(position);
        String displayName = mDataLoader.getDisplayNameAt(position);

        String sub = imageUrl.substring(31);
        String result = "http://cdn.meme.am/images/150x/" + sub;

        viewHolder.textView.setText(displayName);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext) //
                .load(result) //
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
}