/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sebbas.android.memegenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.MainViewHolder> implements Filterable {

    private static final String TAG = "SimpleRecyclerAdapter";
    public static final String TOPICS_URL = "Topics";

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CONTENT = 0;

    private Context mContext;
    private LayoutInflater mInflater;
    private DataLoader mDataLoader;
    private int mLayoutMode;
    private RecyclerFragment mFragment;
    private List<LineItem> mLineItems = null;
    private List<Integer> mAllowedLineItemPositions = null;
    private int mPageIndex = 0;

    public SimpleRecyclerAdapter(Fragment fragment) {
        mContext = fragment.getActivity();
        mFragment = (RecyclerFragment) fragment;
        mLayoutMode = mFragment.getLayoutMode();
        mDataLoader = new DataLoader(fragment, mFragment.getFragmentType());
        mInflater = LayoutInflater.from(mContext);
        mLineItems = new ArrayList<>();
        mAllowedLineItemPositions = new ArrayList<>();

        // Trigger initial data load
        refreshData();
    }

    @Override
    public int getItemViewType(int position) {
        if (mLayoutMode != UIOptions.SCROLLBOX_LAYOUT) {
            return mLineItems.get(position).isHeaderItem() ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
        }
        return VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MainViewHolder.ViewHolderCallback mainViewHolderCallback = new MainViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                // Only trigger click event for content items
                if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
                    ((ItemClickCallback) mContext).onItemClick(position, mLineItems);
                }
            }
        };

        View view;

        switch (mLayoutMode) {
            case UIOptions.LIST_LAYOUT:
                if (viewType == VIEW_TYPE_HEADER) {
                    view = mInflater.inflate(R.layout.header_item, parent, false);
                } else {
                    view = mInflater.inflate(R.layout.list_item, parent, false);
                }
                return new ListViewHolder(view, mainViewHolderCallback);
            case UIOptions.CARD_LAYOUT:
                view = mInflater.inflate(R.layout.card_item, parent, false);
                return new CardViewHolder(view, mainViewHolderCallback);
            case UIOptions.GRID_LAYOUT:
                view = mInflater.inflate(R.layout.grid_item, parent, false);
                return new GridViewHolder(view, mainViewHolderCallback);
            case UIOptions.SCROLLBOX_LAYOUT:
                view = mInflater.inflate(R.layout.horizontal_scroll, parent, false);
                return new ScrollBoxViewHolder(view);
            default:
                view = mInflater.inflate(R.layout.list_item, parent, false);
                return new ListViewHolder(view, mainViewHolderCallback);
        }
    }

    @Override
    public void onBindViewHolder(final MainViewHolder viewHolder, int position) {
        final LineItem item = mLineItems.get(position);
        final View itemView = viewHolder.itemView;
        final GridSLM.LayoutParams lp = new GridSLM.LayoutParams(itemView.getLayoutParams());

        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(item.getSectionFirstPosition());
        itemView.setLayoutParams(lp);

        viewHolder.textViewTitle.setText(item.getImageUrl());

        if (mLayoutMode == UIOptions.LIST_LAYOUT && getItemViewType(position) == VIEW_TYPE_CONTENT) {
            ListViewHolder listViewHolder = (ListViewHolder) viewHolder;

            Transformation roundedTransformation = new RoundedTransformationBuilder()
                    .oval(true)
                    .build();

            PicassoCache.getPicassoInstance(mContext)
                    .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), UIOptions.THUMBNAIL_SIZE_LIST))
                    .placeholder(R.color.invisible)
                    .error(android.R.color.holo_red_dark)
                    .fit()
                    .transform(roundedTransformation)
                    .centerCrop()
                    .tag(mContext)
                    .into(listViewHolder.imageView);
        }

        if (mLayoutMode == UIOptions.CARD_LAYOUT) {
            final CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;

            Transformation transformation = new Transformation() {

                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = cardViewHolder.imageView.getWidth();

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);

                    /*int[] maxTextureSize = new int[1];
                    gl.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
                    int maxTexture = maxTextureSize[0];*/

                    if (targetHeight > Utils.DEFAULT_MAX_BITMAP_DIMENSION) {
                        targetHeight = Utils.DEFAULT_MAX_BITMAP_DIMENSION;
                        targetWidth = (int) (targetHeight / aspectRatio);
                    }
                    if (targetWidth > Utils.DEFAULT_MAX_BITMAP_DIMENSION) {
                        targetWidth = Utils.DEFAULT_MAX_BITMAP_DIMENSION;
                        targetHeight = (int) (targetWidth * aspectRatio);
                    }

                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "transformation" + " desiredWidth";
                }
            };

            PicassoCache.getPicassoInstance(mContext)
                    .load(item.getImageUrl())//(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_LARGE))
                    .placeholder(android.R.color.holo_blue_bright)
                    .error(android.R.color.holo_red_dark)
                    .tag(mContext)
                    .resize(0, Utils.DEFAULT_MAX_BITMAP_DIMENSION)
                    .transform(transformation)
                    .into(cardViewHolder.imageView);

            cardViewHolder.textViewViewCount.setText(Utils.getViewCountString(mContext, item.getViewCount()));
            cardViewHolder.textViewTimeStamp.setText(Utils.getTimeAgoString(mContext, Integer.valueOf(item.getTimeStamp())));
        }

        if (mLayoutMode == UIOptions.SCROLLBOX_LAYOUT) {
            ScrollBoxViewHolder scrollBoxViewHolder = (ScrollBoxViewHolder) viewHolder;

            // Setup horizontal scroll box
            scrollBoxViewHolder.horizontalRecyclerView.setAdapter(
                    new HorizontalRecyclerAdapter(mFragment, position));
            scrollBoxViewHolder.horizontalRecyclerView.setLayoutManager(
                    new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
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

                for (int i = 0; i < mDataLoader.getItemCount(); i++) {
                    String currentLineItemTitle = mDataLoader.getTitleAt(i);

                    if (!Utils.stringPatternMatch(currentLineItemTitle, (String) constraint)) {
                        filteredItems.add(i);
                    }
                }
                return filteredItems;
            }
        };
    }

    abstract static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ViewHolderCallback mViewHolderCallback;

        public MainViewHolder(View view) {
            super(view);
            textViewTitle = (TextView) view.findViewById(R.id.item_title);
        }

        public MainViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            this(view);
            mViewHolderCallback = viewHolderCallback;
        }

        interface ViewHolderCallback {
            void onItemClick(int position);
        }
    }

    static class CardViewHolder extends MainViewHolder implements View.OnClickListener {
        GifImageView imageView;
        TextView textViewViewCount;
        TextView textViewTimeStamp;

        public CardViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);

            imageView = (GifImageView) view.findViewById(R.id.item_image);
            textViewViewCount = (TextView) view.findViewById(R.id.item_viewcount);
            textViewTimeStamp = (TextView) view.findViewById(R.id.item_datetime);
        }

        @Override
        public void onClick(View v) {
            //GifDrawable gifDrawable = (GifDrawable) imageView.getDrawable();
            //gifDrawable.start();
            mViewHolderCallback.onItemClick(getPosition());
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

    static class GridViewHolder extends MainViewHolder implements View.OnClickListener {
        ImageView imageView;

        public GridViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);
            imageView = (ImageView) view.findViewById(R.id.item_image);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }
    }

    static class ScrollBoxViewHolder extends MainViewHolder {
        RecyclerView horizontalRecyclerView;

        public ScrollBoxViewHolder(View view) {
            super(view);
            horizontalRecyclerView = (RecyclerView) view.findViewById(R.id.horizontal_recyclerview);
        }
    }

    private List<LineItem> getLineItems() {
        return mDataLoader.getLineItems(mAllowedLineItemPositions, mLayoutMode);
    }

    private String getCurrentPageDataUrl() {
        int fragmentType = mFragment.getFragmentType();

        switch (fragmentType) {
            case RecyclerFragment.QUERY:
                String query = mFragment.getQuery();
                return Utils.getUrlForQuery(mPageIndex, query);
            case RecyclerFragment.EXPLORE:
                return TOPICS_URL;
            default:
                return Utils.getUrlForData(mPageIndex, fragmentType);
        }
    }

    private void preloadImages() {
        Log.d(TAG, "size is: " + mLineItems.size());
        for (int i = 0; i < mLineItems.size(); i++) {
            LineItem item = mLineItems.get(i);
            PicassoCache.getPicassoInstance(mContext)
                    .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), UIOptions.THUMBNAIL_SIZE_CARD))
                    .fetch();
        }
    }

    public void refreshUI() {
        mLineItems = getLineItems();
        notifyDataSetChanged();
    }

    public void refreshData() {
        String url = getCurrentPageDataUrl();
        mDataLoader.load(url);
    }

}
