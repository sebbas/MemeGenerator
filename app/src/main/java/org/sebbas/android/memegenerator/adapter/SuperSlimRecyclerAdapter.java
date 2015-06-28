package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tonicartos.superslim.GridSLM;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;

import java.util.ArrayList;

public class SuperSlimRecyclerAdapter extends RecyclerFragmentAdapter {

    private static final int VIEW_TYPE_FILLER = 0;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CONTENT = 2;

    private Context mContext;

    public SuperSlimRecyclerAdapter(Context context, ArrayList<LineItem> lineItems) {
        super(lineItems);
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FILLER;
        } else {
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
                    ((FragmentCallback) mContext).onItemClick(
                            SuperSlimRecyclerAdapter.super.getContentPosition(position),
                            SuperSlimRecyclerAdapter.super.mLineItems);
                }
            }
        };

        View view = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case VIEW_TYPE_FILLER:
                view = inflater.inflate(R.layout.toolbar_padding, parent, false);
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

                Glide.with(mContext)
                        .load(item.getImageUrl()) //.load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_MEDIUM))
                        .centerCrop()
                        .into(listViewHolder.imageView);
            }
        }
    }

    static class ListViewHolder extends MainViewHolder implements View.OnClickListener {
        RoundedImageView imageView;

        public ListViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);

            imageView = (RoundedImageView) view.findViewById(R.id.item_image);
        }

        @Override
        public void onClick(View v) {
            super.mViewHolderCallback.onItemClick(getPosition());
        }
    }
}
