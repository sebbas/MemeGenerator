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


public class VerticalRecyclerAdapter extends RecyclerView.Adapter<VerticalRecyclerAdapter.ViewHolder> {

    private static final String TAG = "VerticalRecyclerAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mTitles = null;

    public VerticalRecyclerAdapter(Fragment fragment, List<String> titles) {
        mContext = fragment.getActivity();
        mInflater = LayoutInflater.from(mContext);
        mTitles = titles;
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.list_item_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String title = mTitles.get(position);

        viewHolder.textViewTitle.setText(title);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;

        public ViewHolder(View view) {
            super(view);

            textViewTitle = (TextView) view.findViewById(R.id.item_title);

        }
    }
}
