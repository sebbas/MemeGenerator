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

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

import java.util.List;


public class HorizontalRecyclerAdapter extends RecyclerView.Adapter<HorizontalRecyclerAdapter.ViewHolder> {

    private static final String TAG = "HorizontalRecyclerAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<LineItem> mLineItems = null;

    public HorizontalRecyclerAdapter(Fragment fragment, int position) {
        mContext = fragment.getActivity();
        mInflater = LayoutInflater.from(mContext);

        RecyclerFragment recyclerFragment = (RecyclerFragment) fragment;
        int fragmentType = recyclerFragment.getFragmentType();

        LocalDataLoader localDataLoader = new LocalDataLoader(fragment, fragmentType, position);
        mLineItems = localDataLoader.getLineItems();
    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        ViewHolder.ViewHolderCallback viewHolderCallback = new ViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                ((ItemClickCallback) mContext).onItemClick(position, mLineItems);
            }
        };

        View view = mInflater.inflate(R.layout.list_item_horizontal, parent, false);
        return new ViewHolder(view, viewHolderCallback);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final LineItem item = mLineItems.get(position);

        viewHolder.textViewTitle.setText(item.getTitle());

        Transformation roundedTransformation = new RoundedTransformationBuilder()
                .oval(true)
                .build();

        PicassoCache.getPicassoInstance(mContext)
                .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_MEDIUM))
                .placeholder(android.R.color.holo_blue_bright)
                .error(android.R.color.holo_red_dark)
                .fit()
                .transform(roundedTransformation)
                .centerCrop()
                .tag(mContext)
                .into(viewHolder.imageView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textViewTitle;
        ViewHolderCallback mViewHolderCallback;

        public ViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view);
            view.setOnClickListener(this);

            imageView = (ImageView) view.findViewById(R.id.item_image);
            textViewTitle = (TextView) view.findViewById(R.id.item_title);

            mViewHolderCallback = viewHolderCallback;
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getPosition());
        }

        interface ViewHolderCallback {
            void onItemClick(int position);
        }

    }
}
