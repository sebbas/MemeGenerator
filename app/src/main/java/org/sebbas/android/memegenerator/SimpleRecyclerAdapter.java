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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    private Context mContext;
    private ViewPagerRecyclerViewFragment mFragment;
    private LayoutInflater mInflater;
    private DataLoader mDataLoader;
    private Picasso mPicasso;

    public SimpleRecyclerAdapter(Fragment fragment, DataLoader dataLoader) {
        mContext = fragment.getActivity();
        mFragment = (ViewPagerRecyclerViewFragment) fragment;
        mDataLoader = dataLoader;
        mInflater = LayoutInflater.from(mContext);
        OkHttpClient okHttpClient = new OkHttpClient();
        DiskLruImageCache diskLruImageCache = new DiskLruImageCache(mContext, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE, Bitmap.CompressFormat.JPEG, 0);
        mPicasso = new Picasso.Builder(diskLruImageCache)
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();
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

        /*final int positionFinal = position;

        // Set local thumbnail
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String imageKey = mFragment.getFragmentType() + "_" + positionFinal;
                DiskLruImageCache diskLruImageCache = ((MainActivity) mContext).getDiskLruImageCache();
                Bitmap bitmap = diskLruImageCache.getBitmap(imageKey);

                // Get placeholder bitmap if no bitmap was found in the disk cache
                Bitmap result;
                if (bitmap == null) {
                    result = BitmapFactory.decodeResource(mContext.getResources(), android.R.color.white);
                } else {
                    result = bitmap;
                }
                viewHolder.imageView.setImageBitmap(result);
            }
        });
        t.run();*/

        // Get an updated image over the network and replace the just set thumbnail with it
        String displayName = mDataLoader.getDisplayNameAt(position);
        String imageUrl = mDataLoader.getImageUrlAt(position);

        viewHolder.textView.setText(displayName);

        /*Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Set the bitmap in imageview
                BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                viewHolder.imageView.setBackground(bitmapDrawable);

                // Store bitmap in cache for later use
                diskLruImageCache.put(imageKey, bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };*/

        // Trigger the download of the URL asynchronously into the image view.
        /*Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(android.R.color.white)
                .error(android.R.color.white)
                .fit()
                .centerCrop()
                .tag(mContext)
                .into(viewHolder.imageView);*/
        PicassoCache.getPicassoInstance(mContext).load(imageUrl).into(viewHolder.imageView);
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
