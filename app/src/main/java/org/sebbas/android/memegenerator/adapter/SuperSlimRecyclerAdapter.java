package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.squareup.picasso.Transformation;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.PicassoCache;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.dataloader.JsonDataLoader;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;

import java.util.ArrayList;
import java.util.List;

public class SuperSlimRecyclerAdapter extends RecyclerFragmentAdapter {

    private static final int VIEW_TYPE_FILLER = 0;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CONTENT = 2;
    private static final int CORNER_RADIUS = 250;

    private Context mContext;
    private List<Integer> mAllowedLineItemPositions;
    private List<LineItem> mLineItems = null;
    private JsonDataLoader mJsonDataLoader;
    private int mPageIndex = 0;
    private RecyclerFragment mFragment;

    public SuperSlimRecyclerAdapter(RecyclerFragment fragment) {
        mContext = fragment.getActivity();
        mFragment = fragment;
        mAllowedLineItemPositions = new ArrayList<>();
        mLineItems = new ArrayList<>();

        String fragmentType = fragment.getFragmentType();

        mJsonDataLoader = new JsonDataLoader(fragment, fragmentType);
        refreshData();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FILLER;
        } else {
            if (mLineItems.get(position).isHeaderItem()) {
                return VIEW_TYPE_HEADER;
            } else {
                return VIEW_TYPE_CONTENT;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ListViewHolder.ViewHolderCallback viewHolderCallback = new ListViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                // Only trigger click event for content items
                if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
                    ((ItemClickCallback) mContext).onItemClick(position, mLineItems);
                }
            }
        };

        View view = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case VIEW_TYPE_FILLER:
                view = inflater.inflate(R.layout.recycler_header_small, parent, false);
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
        final LineItem item = mLineItems.get(position);
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
                        .transform(trans)
                        .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(),
                                UIOptions.THUMBNAIL_SIZE_LIST));
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

    @Override
    public Filter getFilter() {
        return new Filter() {

            private List<Integer> filteredResult;

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                filteredResult = getFilteredResults(charSequence);

                FilterResults results = new FilterResults();
                results.values = filteredResult;
                results.count = filteredResult.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mAllowedLineItemPositions = (ArrayList<Integer>) filterResults.values;
                refreshUI();
            }


            private List<Integer> getFilteredResults(CharSequence constraint){
                if (constraint.length() == 0) {
                    mAllowedLineItemPositions.clear();
                    return mAllowedLineItemPositions;
                }

                ArrayList<Integer> filteredItems = new ArrayList<>();

                for (int i = 0; i < mJsonDataLoader.getItemCount(); i++) {
                    String currentLineItemTitle = mJsonDataLoader.getTitleAt(i);

                    if (!Utils.stringPatternMatch(currentLineItemTitle, (String) constraint)) {
                        filteredItems.add(i);
                    }
                }
                return filteredItems;
            }
        };
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

    @Override
    protected List<LineItem> getLineItems() {
        return mJsonDataLoader.getLineItems(mAllowedLineItemPositions, true);
    }

    @Override
    public void refreshUI() {
        mLineItems = getLineItems();
        notifyDataSetChanged();
    }

    @Override
    public void refreshData() {
        String url = Utils.getUrlForData(mPageIndex, mFragment);
        mJsonDataLoader.load(url);
    }
}
