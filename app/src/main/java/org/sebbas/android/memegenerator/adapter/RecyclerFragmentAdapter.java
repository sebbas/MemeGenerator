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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tonicartos.superslim.GridSLM;

import org.sebbas.android.memegenerator.BubbleTextGetter;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.TopicsDetailGridAdapter;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecyclerFragmentAdapter extends
        RecyclerView.Adapter<RecyclerFragmentAdapter.MainViewHolder> implements SectionIndexer, BubbleTextGetter {

    private static final String TAG = "RecyclerFragmentAdapter";
    private static final int VIEW_TYPE_FILLER = 0;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CARD = 2;
    private static final int VIEW_TYPE_SUPER_SLIM = 3;
    private static final int VIEW_TYPE_TOPICS = 4;

    private ArrayList<LineItem> mLineItems;
    private List<Character> mSectionItems;
    private Context mContext;
    private int mItemType;

    public RecyclerFragmentAdapter(Context context, ArrayList<LineItem> lineItems, int itemType) {
        mContext = context;
        mLineItems = lineItems;
        mSectionItems = getSectionItems();
        mItemType = itemType;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FILLER;
        }

        if (mLineItems.get(position).isHeaderItem()) {
            return VIEW_TYPE_HEADER;
        }

        switch (mItemType) {
            case RecyclerFragment.CARD:
                return VIEW_TYPE_CARD;
            case RecyclerFragment.SUPER_SLIM:
                return VIEW_TYPE_SUPER_SLIM;
            case RecyclerFragment.EXPLORE:
                return VIEW_TYPE_TOPICS;
            default:
                return VIEW_TYPE_CARD;
        }
    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
    }

    @Override
    public RecyclerFragmentAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MainViewHolder.ViewHolderCallback viewHolderCallback = new MainViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                // Only trigger click event for content items
                if (getItemViewType(position) == VIEW_TYPE_CARD ||
                        getItemViewType(position) == VIEW_TYPE_SUPER_SLIM ||
                        getItemViewType(position) == VIEW_TYPE_TOPICS) {

                    ((FragmentCallback) mContext).onItemClick(
                            getContentPosition(position),
                            getContentItems(mLineItems));
                }
            }
        };

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        MainViewHolder mainViewHolder;

        switch (viewType) {
            case VIEW_TYPE_FILLER:
                view = inflater.inflate(R.layout.toolbar_padding, parent, false);
                mainViewHolder = new SuperSlimViewHolder(view, viewHolderCallback);
                break;
            case VIEW_TYPE_HEADER:
                view = inflater.inflate(R.layout.header_item, parent, false);
                mainViewHolder = new SuperSlimViewHolder(view, viewHolderCallback);
                break;
            case VIEW_TYPE_CARD:
                view = inflater.inflate(R.layout.squared_item, parent, false);
                mainViewHolder = new CardViewHolder(view, viewHolderCallback);
                break;
            case VIEW_TYPE_SUPER_SLIM:
                view = inflater.inflate(R.layout.rounded_item, parent, false);
                mainViewHolder = new SuperSlimViewHolder(view, viewHolderCallback);
                break;
            case VIEW_TYPE_TOPICS:
                view = inflater.inflate(R.layout.topics_item, parent, false);
                mainViewHolder = new TopicsHeaderViewHolder(view, viewHolderCallback);
                break;
            default:
                view = inflater.inflate(R.layout.toolbar_padding, parent, false);
                mainViewHolder = new SuperSlimViewHolder(view, viewHolderCallback);
                break;
        }
        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerFragmentAdapter.MainViewHolder viewHolder, int position) {
        LineItem item = mLineItems.get(position);
        final View itemView = viewHolder.itemView;
        final GridSLM.LayoutParams lp = new GridSLM.LayoutParams(itemView.getLayoutParams());

        lp.setSlm(GridSLM.ID);
        lp.setColumnWidth(mContext.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
        lp.setFirstPosition(item.getSectionFirstPosition());
        itemView.setLayoutParams(lp);

        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                // Set title letter for header items
                viewHolder.textViewTitle.setText(item.getTitle());
                break;
            case VIEW_TYPE_CARD:
                // Set text and image
                CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;
                Glide.with(mContext)
                        .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_MEDIUM))
                        .asBitmap()
                        .centerCrop()
                        .into(cardViewHolder.imageView);
                break;
            case VIEW_TYPE_SUPER_SLIM:
                // Set text and image
                SuperSlimViewHolder superSlimViewHolder = (SuperSlimViewHolder) viewHolder;
                superSlimViewHolder.textViewTitle.setText(item.getTitle());
                Glide.with(mContext)
                        .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_MEDIUM))
                        .asBitmap()
                        .centerCrop()
                        .into(superSlimViewHolder.imageView);
                break;
            case VIEW_TYPE_TOPICS:
                TopicsHeaderViewHolder topicsHeaderViewHolder = (TopicsHeaderViewHolder) viewHolder;
                topicsHeaderViewHolder.textViewTitle.setText(item.getTitle());
                Glide.with(mContext)
                        .load(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_SMALL))
                        .asBitmap()
                        .centerCrop()
                        .into(topicsHeaderViewHolder.imageView);
                topicsHeaderViewHolder.gridView.setAdapter(new TopicsDetailGridAdapter(mContext, new ArrayList<>(randomSubList(getContentItems(mLineItems), 6)) /*new ArrayList<>(mLineItems.subList(1, 6))*/));

                break;
        }
    }

    public static <T> List<T> randomSubList(List<T> list, int newSize) {
        list = new ArrayList<>(list);
        Collections.shuffle(list);
        return list.subList(0, newSize);
    }

    @Override
    public String getTextToShowInBubble(int position) {
        return Character.toString(mLineItems.get(position).getTitle().charAt(0));
    }

    public static class TopicsHeaderViewHolder extends RecyclerFragmentAdapter.MainViewHolder implements View.OnClickListener, AdapterView.OnItemClickListener {
        ImageView imageView;
        GridView gridView;

        public TopicsHeaderViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);

            imageView = (ImageView) view.findViewById(R.id.item_image);
            gridView = (GridView) view.findViewById(R.id.topics_grid);
            gridView.setOnItemClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getLayoutPosition());
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mViewHolderCallback.onItemClick(getLayoutPosition() + position);
        }
    }

    static class CardViewHolder extends RecyclerFragmentAdapter.MainViewHolder implements View.OnClickListener {
        ImageView imageView;

        public CardViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);

            imageView = (ImageView) view.findViewById(R.id.item_image);
        }

        @Override
        public void onClick(View v) {
            mViewHolderCallback.onItemClick(getLayoutPosition());
        }
    }

    static class SuperSlimViewHolder extends RecyclerFragmentAdapter.MainViewHolder implements View.OnClickListener {
        RoundedImageView imageView;

        public SuperSlimViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);

            imageView = (RoundedImageView) view.findViewById(R.id.item_image);
        }

        @Override
        public void onClick(View v) {
            super.mViewHolderCallback.onItemClick(getLayoutPosition());
        }
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

    // Returns position of item not regarding any padding or header items in line item list
    private int getContentPosition(int position) {
        return position - mLineItems.get(position).getHeaderCount();
    }

    private ArrayList<LineItem> getContentItems(ArrayList<LineItem> lineItems) {
        ArrayList<LineItem> contentItems = new ArrayList<>();
        for (LineItem lineItem : lineItems) {
            if (!lineItem.isHeaderItem()) {
                contentItems.add(lineItem);
            }
        }
        return contentItems;
    }
}
