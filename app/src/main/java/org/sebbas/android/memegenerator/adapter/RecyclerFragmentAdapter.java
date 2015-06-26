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
import android.util.Log;
import android.view.View;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

import java.util.ArrayList;
import java.util.List;


public abstract class RecyclerFragmentAdapter extends
        RecyclerView.Adapter<RecyclerFragmentAdapter.MainViewHolder> implements SectionIndexer {

    private static final String TAG = "RecyclerFragmentAdapter";

    protected ArrayList<LineItem> mLineItems;
    protected List<Character> mSectionItems;

    public RecyclerFragmentAdapter(ArrayList<LineItem> lineItems) {
        mLineItems = lineItems;
        mSectionItems = getSectionItems();
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

    public void refreshUI() {
        this.notifyDataSetChanged();
    }

    public void setLineItems(ArrayList <LineItem> lineItems) {
        mLineItems.clear();
        mLineItems = lineItems;
    }

    public int getLineItemCount() {
        return  mLineItems.size();
    }

    public ArrayList<Character> getSectionItems() {
        ArrayList<Character> sectionItems = new ArrayList<>();
        char headerCurrentHeaderLetter = '\0';

        for (int i = 0; i < mLineItems.size(); i++) {
            LineItem lineItem = mLineItems.get(i);
            char itemLetter = Utils.getTitleLetter(lineItem.getTitle());

            // New section begins if either header letter is empty (1st section) or item letter
            // does not match current header letter
            if (headerCurrentHeaderLetter == '\0' || itemLetter != headerCurrentHeaderLetter) {
                headerCurrentHeaderLetter = itemLetter;

                sectionItems.add(itemLetter);
            }
        }
        return sectionItems;
    }

    @Override
    public Object[] getSections() {
        return mSectionItems.toArray();
    }

    @Override
    public int getPositionForSection(int sectionNumber) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int itemPosition) {
        if (itemPosition >= mLineItems.size()) {
            itemPosition = mLineItems.size() - 1;
        }

        LineItem lineItem = mLineItems.get(itemPosition);
        String title = lineItem.getTitle();
        char letter = Utils.getTitleLetter(title);

        int letterPosition = mSectionItems.indexOf(letter);

        return letterPosition;
    }
}
