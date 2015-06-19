package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sebbas.android.memegenerator.SquaredImageView;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.PicassoCache;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;

import java.util.ArrayList;


public class HorizontalRecyclerAdapter extends RecyclerView.Adapter<HorizontalRecyclerAdapter.ViewHolder> {

    private static final String TAG = "HorizontalRecyclerAdapter";

    private Context mContext;
    private ArrayList<LineItem> mLineItems = null;

    public HorizontalRecyclerAdapter(RecyclerFragment fragment, int position) {
        mContext = fragment.getActivity();

    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        ViewHolder.ViewHolderCallback viewHolderCallback = new ViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                ((FragmentCallback) mContext).onItemClick(position, mLineItems);
            }
        };

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_horizontal, parent, false);
        return new ViewHolder(view, viewHolderCallback);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final LineItem item = mLineItems.get(position);

        viewHolder.textViewTitle.setText(item.getTitle());

        PicassoCache.getPicassoInstance(mContext)
                .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_MEDIUM))
                .placeholder(android.R.color.holo_blue_bright)
                .error(android.R.color.holo_red_dark)
                .fit()
                //.transform(roundedTransformation)
                .centerCrop()
                .tag(mContext)
                .into(viewHolder.imageView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SquaredImageView imageView;
        TextView textViewTitle;
        ViewHolderCallback mViewHolderCallback;

        public ViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view);
            view.setOnClickListener(this);

            imageView = (SquaredImageView) view.findViewById(R.id.item_image);
            textViewTitle = (TextView) view.findViewById(R.id.item_title);

            mViewHolderCallback = viewHolderCallback;
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }

        interface ViewHolderCallback {
            void onItemClick(int position);
        }

    }
}
