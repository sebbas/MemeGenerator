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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.MainViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private DataLoader mDataLoader;
    private int mLayoutMode;
    private String mViewsString;

    public SimpleRecyclerAdapter(Fragment fragment, DataLoader dataLoader) {
        mContext = fragment.getActivity();
        mDataLoader = dataLoader;
        mLayoutMode = ((ViewPagerRecyclerViewFragment) fragment).getLayoutMode();
        mInflater = LayoutInflater.from(mContext);

        // String resources for google cards
        mViewsString = mContext.getResources().getString(R.string.image_views);
    }

    @Override
    public int getItemCount() {
        return mDataLoader.getItemCount();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MainViewHolder.ViewHolderCalback mainViewHolderCallback = new MainViewHolder.ViewHolderCalback() {
            @Override
            public void onItemClick(int position) {
                ((ItemClickCallback) mContext).onItemClick(position, mDataLoader);
            }
        };

        View view;
        switch (mLayoutMode) {
            case UIOptions.LIST_LAYOUT:
                view = mInflater.inflate(R.layout.list_item, parent, false);
                return new ListViewHolder(view, mainViewHolderCallback);
            case UIOptions.CARD_LAYOUT:
                view = mInflater.inflate(R.layout.card_item, parent, false);
                return new CardViewHolder(view, mainViewHolderCallback);
            default:
                view = mInflater.inflate(R.layout.list_item, parent, false);
                return new ListViewHolder(view, mainViewHolderCallback);
        }
    }

    @Override
    public void onBindViewHolder(MainViewHolder viewHolder, int position) {

        // Get an updated image over the network and replace the just set thumbnail with it
        String imageTitle = mDataLoader.getImageTitleAt(position);
        String imageUrl = mDataLoader.getImageUrlAt(position);
        String imageId = mDataLoader.getImageIdAt(position);


        viewHolder.textViewTitle.setText(imageTitle);

        if (mLayoutMode == UIOptions.LIST_LAYOUT) {
            PicassoCache.getPicassoInstance(mContext)
                    .load(Utils.getThumbnailUrl(imageUrl, imageId, Data.THUMBNAIL_SIZE_LIST))
                    .placeholder(android.R.color.holo_blue_bright)
                    .error(android.R.color.holo_red_dark)
                    .fit()
                    .centerCrop()
                    .tag(mContext)
                    .into(viewHolder.imageView);
        }

        if (mLayoutMode == UIOptions.CARD_LAYOUT) {
            CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;

            String viewCount = mDataLoader.getViewCountAt(position);
            String timeStamp = mDataLoader.getTimeStampAt(position);

            PicassoCache.getPicassoInstance(mContext)
                    .load(Utils.getThumbnailUrl(imageUrl, imageId, Data.THUMBNAIL_SIZE_CARD))
                    .placeholder(android.R.color.holo_blue_bright)
                    .error(android.R.color.holo_red_dark)
                    .tag(mContext)
                    .into(viewHolder.imageView);

            cardViewHolder.textViewViewCount.setText(viewCount + " " + mViewsString);
            cardViewHolder.textViewTimeStamp.setText(timeStamp);
        }
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {
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

}
