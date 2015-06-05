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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.sebbas.android.memegenerator.R;


public abstract class RecyclerFragmentAdapter extends RecyclerView.Adapter<RecyclerFragmentAdapter.MainViewHolder> {

    private static final String TAG = "RecyclerFragmentAdapter";

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

    public void refreshUI() {
        this.notifyDataSetChanged();
    }
}
