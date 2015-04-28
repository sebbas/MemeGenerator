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
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.MainViewHolder> implements Filterable {

    private static final String TAG = "SimpleRecyclerAdapter";
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CONTENT = 0;

    private Context mContext;
    private LayoutInflater mInflater;
    private DataLoader mDataLoader;
    private String mViewsString;
    private int mLayoutMode;
    private ViewPagerRecyclerFragment mFragment;
    private List<LineItem> mLineItems = null;
    private ArrayList<LineItem> mFilteredLineItems = null;
    private int mPageIndex = 0;


    public SimpleRecyclerAdapter(Fragment fragment) {
        mContext = fragment.getActivity();
        mFragment = (ViewPagerRecyclerFragment) fragment;
        mLayoutMode = mFragment.getLayoutMode();
        mDataLoader = new DataLoader(fragment, mFragment.getFragmentType());
        mInflater = LayoutInflater.from(mContext);
        mLineItems = new ArrayList<>();

        // String resources for google cards
        mViewsString = mContext.getResources().getString(R.string.image_views);

        // Trigger initial data load
        refreshData();

        // Fill up array list of line items with data from dataloader
        mLineItems = getLineItems();
    }

    @Override
    public int getItemViewType(int position) {
        return mLineItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MainViewHolder.ViewHolderCalback mainViewHolderCallback = new MainViewHolder.ViewHolderCalback() {
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
            default:
                view = mInflater.inflate(R.layout.list_item, parent, false);
                return new ListViewHolder(view, mainViewHolderCallback);
        }
    }

    @Override
    public void onBindViewHolder(MainViewHolder viewHolder, int position) {
        final LineItem item = mLineItems.get(position);
        final View itemView = viewHolder.itemView;

        final GridSLM.LayoutParams lp = new GridSLM.LayoutParams(itemView.getLayoutParams());
        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);

        viewHolder.textViewTitle.setText(item.title);

        if (mLayoutMode == UIOptions.LIST_LAYOUT && getItemViewType(position) == VIEW_TYPE_CONTENT) {

            Transformation transformation = new RoundedTransformationBuilder()
                    .oval(true)
                    .build();

            PicassoCache.getPicassoInstance(mContext)
                    .load(item.imageUrl)//(Utils.getThumbnailUrl(item.imageUrl, item.imageId, UIOptions.THUMBNAIL_SIZE_LIST))
                    .placeholder(R.color.invisible)
                    .error(android.R.color.holo_red_dark)
                    .fit()
                    .transform(transformation)
                    .centerCrop()
                    .tag(mContext)
                    .into(viewHolder.imageView);
        }

        if (mLayoutMode == UIOptions.CARD_LAYOUT) {
            CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;

            PicassoCache.getPicassoInstance(mContext)
                    .load(Utils.getThumbnailUrl(item.imageUrl, item.imageId, UIOptions.THUMBNAIL_SIZE_CARD))
                    .placeholder(android.R.color.holo_blue_bright)
                    .error(android.R.color.holo_red_dark)
                    .tag(mContext)
                    .into(viewHolder.imageView);

            cardViewHolder.textViewViewCount.setText(item.viewCount + " " + mViewsString);
            cardViewHolder.textViewTimeStamp.setText(item.timeStamp);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            List<LineItem> filteredResult;

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                filteredResult = getFilteredResults(charSequence);

                FilterResults results = new FilterResults();
                results.values = filteredResult;
                //results.count = filteredResult.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredLineItems = (ArrayList<LineItem>) filterResults.values;
                refreshUI();
            }


            private ArrayList<LineItem> getFilteredResults(CharSequence constraint){
                if (constraint.length() == 0) {
                    return null;
                }

                ArrayList<LineItem> listResult = new ArrayList<>();
                for (int i = 0; i < mLineItems.size(); i++) {
                    LineItem currentLineItem = mLineItems.get(i);
                    String currentLineItemTitle = currentLineItem.title;

                    if (Utils.stringPatternMatch(currentLineItemTitle, (String) constraint)) {
                        listResult.add(currentLineItem);
                    }
                }

                return listResult;
            }
        };
    }

    abstract static class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        ViewHolderCalback mViewHolderCallback;

        public MainViewHolder(View view, ViewHolderCalback viewHolderCalback) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.item_image);
            textViewTitle = (TextView) view.findViewById(R.id.item_title);

            mViewHolderCallback = viewHolderCalback;
        }

        static interface ViewHolderCalback {
            public void onItemClick(int position);
        }
    }

    static class CardViewHolder extends MainViewHolder implements View.OnClickListener{
        TextView textViewViewCount;
        TextView textViewTimeStamp;


        public CardViewHolder(View view, ViewHolderCalback viewHolderCalback) {
            super(view, viewHolderCalback);
            view.setOnClickListener(this);

            textViewViewCount = (TextView) view.findViewById(R.id.item_viewcount);
            textViewTimeStamp = (TextView) view.findViewById(R.id.item_datetime);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }
    }

    static class ListViewHolder extends MainViewHolder implements View.OnClickListener {

        public ListViewHolder(View view, ViewHolderCalback viewHolderCalback) {
            super(view, viewHolderCalback);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }
    }

    static class GridViewHolder extends MainViewHolder implements View.OnClickListener {

        public GridViewHolder(View view, ViewHolderCalback viewHolderCalback) {
            super(view, viewHolderCalback);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }
    }

    static class LineItem {

        public int sectionManager;
        public int sectionFirstPosition;
        public boolean isHeader;
        public String title;
        public String imageUrl;
        public String imageId;
        public String viewCount;
        public String timeStamp;

        // Section header item constructor
        public LineItem(String title, boolean isHeader, int sectionManager,
                        int sectionFirstPosition) {
            this.title = title;
            this.isHeader = isHeader;
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;
        }

        // Regular item constructor
        public LineItem(String title, String imageUrl, String imageId, String viewCount,
                        String timeStamp, boolean isHeader, int sectionManager,
                        int sectionFirstPosition) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.imageId = imageId;
            this.viewCount = viewCount;
            this.timeStamp = timeStamp;
            this.isHeader = isHeader;
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;
        }
    }

    private List<LineItem> getLineItems() {
        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;
        int lineItemCount = 0;

        String title, header, imageUrl, imageId, viewCount, timeStamp;
        ArrayList<LineItem> lineItems = new ArrayList<>();

        if (isFiltering()) {
            lineItemCount = mFilteredLineItems.size();
        } else {
            lineItemCount = mDataLoader.getItemCount();
        }

        for (int i = 0; i < lineItemCount; i++) {

            // Get some information (used for regular and/or header items)
            if (isFiltering()) {
                title = mFilteredLineItems.get(i).title;
                header = Utils.getScrollHeaderTitleLetter(title);
                imageUrl = mFilteredLineItems.get(i).imageUrl;
                imageId = mFilteredLineItems.get(i).imageId;
                viewCount = mFilteredLineItems.get(i).viewCount;
                timeStamp = mFilteredLineItems.get(i).timeStamp;
            } else {
                title = mDataLoader.getImageTitleAt(i);
                header = Utils.getScrollHeaderTitleLetter(title);
                imageUrl = mDataLoader.getImageUrlAt(i);
                imageId = mDataLoader.getImageIdAt(i);
                viewCount = mDataLoader.getViewCountAt(i);
                timeStamp = mDataLoader.getTimeStampAt(i);
            }

            if (!TextUtils.equals(lastHeader, header) && mLayoutMode == UIOptions.LIST_LAYOUT) {
                // Insert new header view and update section data.
                sectionManager = (sectionManager + 1) % 2;
                sectionFirstPosition = i + headerCount;
                lastHeader = header;
                headerCount += 1;
                lineItems.add(new LineItem(header, true, sectionManager, sectionFirstPosition));
            }
            lineItems.add(new LineItem(title, imageUrl, imageId, viewCount, timeStamp, false,
                    sectionManager, sectionFirstPosition));
        }
        return lineItems;
    }

    private boolean isFiltering() {
        return (mFilteredLineItems != null);
    }

    private String getCurrentPageDataUrl() {
        int fragmentType = mFragment.getFragmentType();
        if (fragmentType == ViewPagerRecyclerFragment.QUERY) {
            String query = mFragment.getQuery();
            return Data.getUrlForQuery(mPageIndex, query);
        }
        return Data.getUrlForData(mPageIndex, fragmentType);
    }

    public void refreshUI() {
        mLineItems = getLineItems();
        notifyDataSetChanged();
    }

    public void refreshData() {
        mDataLoader.load(getCurrentPageDataUrl());
    }
}
