package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koushikdutta.ion.Ion;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.SquaredImageView;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;

import java.util.ArrayList;

public class SimpleRecyclerAdapter extends RecyclerFragmentAdapter {

    private Context mContext;

    public SimpleRecyclerAdapter(RecyclerFragment fragment, ArrayList<LineItem> lineItems) {
        super(fragment, lineItems);
        mContext = fragment.getActivity();
    }

    @Override
    public int getItemCount() {
        return super.mLineItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return VIEW_TYPE_FILLER_TOOLBAR;
            case 1:
                return VIEW_TYPE_FILLER_TABS;
            default:
                return VIEW_TYPE_CONTENT;
        }
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SquareViewHolder.ViewHolderCallback viewHolderCallback = new SquareViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                // Only trigger click event for content items
                if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
                    ((FragmentCallback) mContext).onItemClick(position,
                            SimpleRecyclerAdapter.super.mLineItems);
                }
            }
        };

        View view = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case VIEW_TYPE_FILLER_TOOLBAR:
                view = inflater.inflate(R.layout.padding_toolbar, parent, false);
                break;
            case VIEW_TYPE_FILLER_TABS:
                // Show tabs only if super class says so and device is in portrait mode
                if (super.mWithTabOffset && Utils.isPortraitMode(mContext)) {
                    view = inflater.inflate(R.layout.padding_tabs, parent, false);
                } else {
                    view = inflater.inflate(R.layout.no_padding, parent, false);
                }
                break;
            case VIEW_TYPE_CONTENT:
                view = inflater.inflate(R.layout.squared_item, parent, false);
        }
        return new SquareViewHolder(view, viewHolderCallback);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder viewHolder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            final LineItem item = mLineItems.get(position);

            final SquareViewHolder squareViewHolder = (SquareViewHolder) viewHolder;

            Ion.with(squareViewHolder.imageView)
                    .placeholder(android.R.color.holo_blue_bright)
                    .error(android.R.color.holo_red_dark)
                    .centerCrop()
                    .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(),
                            Utils.IMAGE_SMALL));
        }
    }

    static class SquareViewHolder extends MainViewHolder implements View.OnClickListener {
        SquaredImageView imageView;

        public SquareViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);

            imageView = (SquaredImageView) view.findViewById(R.id.item_image);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }
    }
}
