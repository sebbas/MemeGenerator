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


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    private Context mContext;
    private LayoutInflater mInflater;
    private DataLoader mDataLoader;

    public SimpleRecyclerAdapter(Fragment fragment, DataLoader dataLoader) {
        mContext = fragment.getActivity();
        mDataLoader = dataLoader;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return mDataLoader.getItemCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_googlecards_card, parent, false);
        SimpleRecyclerAdapter.ViewHolder viewHolder = new ViewHolder(view, new ViewHolder.ViewHolderCalback() {
            @Override
            public void onItemClick(int position) {
                ((ItemClickCallback) mContext).onItemClick(position, mDataLoader);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        // Get an updated image over the network and replace the just set thumbnail with it
        String displayName = mDataLoader.getDisplayNameAt(position);
        String imageUrl = mDataLoader.getImageUrlAt(position);

        viewHolder.textView.setText(displayName);

        PicassoCache.getPicassoInstance(mContext)
                .load(imageUrl)
                .placeholder(android.R.color.holo_blue_bright)
                .error(android.R.color.holo_red_dark)
                .fit()
                .centerCrop()
                .tag(mContext)
                .into(viewHolder.imageView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        ImageView imageView;
        ViewHolderCalback mViewHolderCallback;

        public ViewHolder(View view, ViewHolderCalback viewHolderCalback) {
            super(view);
            view.setOnClickListener(this);
            textView = (TextView) view.findViewById(R.id.activity_googlecards_card_textview);
            imageView = (ImageView) view.findViewById(R.id.activity_googlecards_card_imageview);
            mViewHolderCallback = viewHolderCalback;
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }

        public static interface ViewHolderCalback {
            public void onItemClick(int position);
        }
    }
}
