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


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.MainViewHolder> implements Filterable {

    private static final String TAG = "SimpleRecyclerAdapter";
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CONTENT = 0;

    private Context mContext;
    private LayoutInflater mInflater;
    private DataLoader mDataLoader;
    private int mLayoutMode;
    private ViewPagerRecyclerFragment mFragment;
    private List<LineItem> mLineItems = null;
    private List<Integer> mAllowedLineItemPositions = null;
    private int mPageIndex = 0;

    public SimpleRecyclerAdapter(Fragment fragment) {
        mContext = fragment.getActivity();
        mFragment = (ViewPagerRecyclerFragment) fragment;
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
        return mLineItems.get(position).isHeaderItem() ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
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
        lp.setFirstPosition(item.getSectionFirstPosition());
        itemView.setLayoutParams(lp);

        viewHolder.textViewTitle.setText(item.getTitle());

        if (mLayoutMode == UIOptions.LIST_LAYOUT && getItemViewType(position) == VIEW_TYPE_CONTENT) {

            Transformation roundedTransformation = new RoundedTransformationBuilder()
                    .oval(true)
                    .build();

            PicassoCache.getPicassoInstance(mContext)
                    .load(Utils.getThumbnailUrl(item.getImageUrl(), item.getImageId(), UIOptions.THUMBNAIL_SIZE_LIST))
                    .placeholder(R.color.invisible)
                    .error(android.R.color.holo_red_dark)
                    .fit()
                    .transform(roundedTransformation)
                    .centerCrop()
                    .tag(mContext)
                    .into(viewHolder.imageView);
        }

        if (mLayoutMode == UIOptions.CARD_LAYOUT) {
            CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;

            PicassoCache.getPicassoInstance(mContext)
                    .load(Utils.getThumbnailUrl(item.getImageUrl(), item.getImageId(), UIOptions.THUMBNAIL_SIZE_CARD))
                    .placeholder(android.R.color.holo_blue_bright)
                    .error(android.R.color.holo_red_dark)
                    .tag(mContext)
                    .centerInside()
                    .fit()
                    .into(viewHolder.imageView);

            cardViewHolder.textViewViewCount.setText(Utils.getViewCountString(mContext, item.getViewCount()));
            cardViewHolder.textViewTimeStamp.setText(Utils.getTimeAgoString(mContext, Integer.valueOf(item.getTimeStamp())));
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
                    String currentLineItemTitle = mDataLoader.getImageTitleAt(i);

                    if (!Utils.stringPatternMatch(currentLineItemTitle, (String) constraint)) {
                        filteredItems.add(i);
                    }
                }
                return filteredItems;
            }
        };
    }

    abstract static class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        ViewHolderCallback mViewHolderCallback;

        public MainViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.item_image);
            textViewTitle = (TextView) view.findViewById(R.id.item_title);

            mViewHolderCallback = viewHolderCallback;
        }

        static interface ViewHolderCallback {
            public void onItemClick(int position);
        }
    }

    static class CardViewHolder extends MainViewHolder implements View.OnClickListener{
        TextView textViewViewCount;
        TextView textViewTimeStamp;


        public CardViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
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

        public ListViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }
    }

    static class GridViewHolder extends MainViewHolder implements View.OnClickListener {

        public GridViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }
    }

    private List<LineItem> getLineItems() {
        return mDataLoader.getLineItems(mAllowedLineItemPositions, mLayoutMode);
    }

    private String getCurrentPageDataUrl() {
        int fragmentType = mFragment.getFragmentType();
        if (fragmentType == ViewPagerRecyclerFragment.QUERY) {
            String query = mFragment.getQuery();
            return Data.getUrlForQuery(mPageIndex, query);
        }
        return Data.getUrlForData(mPageIndex, fragmentType);
    }

    private void preloadImages() {
        Log.d(TAG, "size is: " + mLineItems.size());
        for (int i = 0; i < mLineItems.size(); i++) {
            LineItem item = mLineItems.get(i);
            PicassoCache.getPicassoInstance(mContext)
                    .load(Utils.getThumbnailUrl(item.getImageUrl(), item.getImageId(), UIOptions.THUMBNAIL_SIZE_CARD))
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
