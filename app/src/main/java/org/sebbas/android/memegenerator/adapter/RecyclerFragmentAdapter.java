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

package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.PicassoCache;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.UIOptions;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.dataloader.JsonDataLoader;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

import java.util.ArrayList;
import java.util.List;


public abstract class RecyclerFragmentAdapter extends RecyclerView.Adapter<RecyclerFragmentAdapter.MainViewHolder> implements Filterable {

    private static final String TAG = "SimpleRecyclerAdapter";
    public static final String TOPICS_URL = "Topics";

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

    static class FillerViewHolder extends RecyclerView.ViewHolder {
        public FillerViewHolder(View view) {
            super(view);
        }
    }

    abstract protected List<LineItem> getLineItems();
    abstract public void refreshUI();
    abstract public void refreshData();
}
