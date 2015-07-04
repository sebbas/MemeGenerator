package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;

import java.util.ArrayList;

public class MoreRecyclerAdapter extends RecyclerView.Adapter<MoreRecyclerAdapter.MoreItemViewHolder> {

    private static final int VIEW_TYPE_FILLER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private Context mContext;
    private String[] mLineItemTitles;
    private TypedArray mLineItemIcons;

    public MoreRecyclerAdapter(Context context, int titleResourceArray, int iconResourceArray) {
        mContext = context;
        mLineItemTitles = context.getResources().getStringArray(titleResourceArray);
        mLineItemIcons = context.getResources().obtainTypedArray(iconResourceArray);
    }

    @Override
    public int getItemCount() {
        return mLineItemTitles.length;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FILLER : VIEW_TYPE_CONTENT;
    }

    @Override
    public MoreRecyclerAdapter.MoreItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MoreItemViewHolder.ViewHolderCallback viewHolderCallback = new MoreItemViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                // Only trigger click event for content items
                if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
                    // TODO fix this
                    ((FragmentCallback) mContext).onItemClick(position, null);
                }
            }
        };

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == VIEW_TYPE_FILLER) {
            view = inflater.inflate(R.layout.toolbar_padding, parent, false);
        } else {
            view = inflater.inflate(R.layout.more_item, parent, false);
        }

        return new MoreItemViewHolder(view, viewHolderCallback);
    }

    @Override
    public void onBindViewHolder(MoreItemViewHolder viewHolder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {

            viewHolder.itemTitle.setText(mLineItemTitles[position]);
            Glide.with(mContext)
                    .load(mLineItemIcons.getResourceId(position, -1))
                    .centerCrop()
                    .into(viewHolder.itemIcon);
        }
    }

    static class MoreItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemTitle;
        ImageView itemIcon;
        ViewHolderCallback mViewHolderCallback;

        public MoreItemViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view);
            view.setOnClickListener(this);

            itemIcon = (ImageView) view.findViewById(R.id.more_icon);
            mViewHolderCallback = viewHolderCallback;

        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getLayoutPosition());
        }

        interface ViewHolderCallback {
            void onItemClick(int position);
        }
    }
}
