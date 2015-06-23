package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.tonicartos.superslim.GridSLM;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;

import java.util.ArrayList;

public class SuperSlimRecyclerAdapter extends RecyclerFragmentAdapter {

    private static final String TAG = "SuperSlimRecyclerAdapter";
    private static final int CORNER_RADIUS = 250;

    private Context mContext;

    public SuperSlimRecyclerAdapter(RecyclerFragment fragment, ArrayList<LineItem> lineItems) {
        super(fragment, lineItems);
        mContext = fragment.getActivity();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return VIEW_TYPE_FILLER_TOOLBAR;
            case 1:
                return VIEW_TYPE_FILLER_TABS;
            default:
                if (super.mLineItems.get(position).isHeaderItem()) {
                    return VIEW_TYPE_HEADER;
                } else {
                    return VIEW_TYPE_CONTENT;
                }
        }
    }

    @Override
    public int getItemCount() {
        return super.mLineItems.size();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ListViewHolder.ViewHolderCallback viewHolderCallback = new ListViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                // Only trigger click event for content items
                if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
                    ((FragmentCallback) mContext).onItemClick(position, SuperSlimRecyclerAdapter.super.mLineItems);
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
                view = inflater.inflate(R.layout.list_item, parent, false);
                break;
            case VIEW_TYPE_HEADER:
                view = inflater.inflate(R.layout.header_item, parent, false);
                break;
        }
        return new ListViewHolder(view, viewHolderCallback);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder viewHolder, int position) {
        final LineItem item = super.mLineItems.get(position);
        final View itemView = viewHolder.itemView;
        final GridSLM.LayoutParams lp = new GridSLM.LayoutParams(itemView.getLayoutParams());

        lp.setSlm(GridSLM.ID);
        lp.setColumnWidth(mContext.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
        lp.setFirstPosition(item.getSectionFirstPosition());
        itemView.setLayoutParams(lp);

        if (getItemViewType(position) == VIEW_TYPE_CONTENT || getItemViewType(position) == VIEW_TYPE_HEADER) {
            final ListViewHolder listViewHolder = (ListViewHolder) viewHolder;
            listViewHolder.textViewTitle.setText(item.getTitle());

            if (getItemViewType(position) == VIEW_TYPE_CONTENT) {

                Transform trans = new Transform() {
                    boolean isOval = false;
                    @Override
                    public Bitmap transform(Bitmap bitmap) {
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, CORNER_RADIUS, CORNER_RADIUS, false);
                        Bitmap transformed = RoundedDrawable
                                .fromBitmap(scaled)
                                .setScaleType(ImageView.ScaleType.CENTER_CROP)
                                .setCornerRadius(CORNER_RADIUS)
                                .setOval(isOval)
                                .toBitmap();
                        if (!bitmap.equals(scaled)) bitmap.recycle();
                        if (!scaled.equals(transformed)) bitmap.recycle();

                        return transformed;
                    }

                    @Override
                    public String key() {
                        return "rounded_radius_" + CORNER_RADIUS + "_oval_" + isOval;
                    }
                };

                Ion.with(listViewHolder.imageView)
                        .placeholder(R.color.invisible)
                        .error(android.R.color.holo_red_dark)
                        .resize(CORNER_RADIUS, CORNER_RADIUS)
                        .centerCrop()
                        .crossfade(true)
                        .transform(trans)
                        .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(),
                                Utils.IMAGE_SMALL));
                /*PicassoCache.getPicassoInstance(mContext)
                        .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), UIOptions.THUMBNAIL_SIZE_LIST))
                        .placeholder(R.color.invisible)
                        .error(android.R.color.holo_red_dark)
                        .fit()
                        .transform(trans)
                        .centerCrop()
                        .tag(mContext)
                        .into(listViewHolder.imageView);*/
            }
        }
    }

    static class ListViewHolder extends MainViewHolder implements View.OnClickListener {
        ImageView imageView;

        public ListViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);

            imageView = (ImageView) view.findViewById(R.id.item_image);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }
    }
}
