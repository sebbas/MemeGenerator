package org.sebbas.android.memegenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class GridViewAdapter extends BaseAdapter {
    private final Context mContext;
    private List<String> mImageUrls = new ArrayList<String>();

    public GridViewAdapter(Context context) {
        mContext = context;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        mImageUrls = imageUrls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(mContext);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext) //
                .load(url) //
                .placeholder(R.drawable.ic_launcher) //
                .error(R.drawable.ic_launcher) //
                .fit() //
                .centerCrop() //
                .tag(mContext) //
                .into(view);

        return view;

        /*LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.gridview_imageview, parent, false);

        SquaredImageView view = null;
        ProgressBar progressBar = null;

        if (convertView != null) {
            view = (SquaredImageView) convertView.findViewById(R.id.squaredImageView);
            view.setScaleType(CENTER_CROP);

            progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext) //
                .load(url) //
                .error(R.drawable.ic_launcher) //
                .fit() //
                .centerCrop() //
                .tag(mContext) //
                .into(view, new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

        return convertView;*/
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

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }
}