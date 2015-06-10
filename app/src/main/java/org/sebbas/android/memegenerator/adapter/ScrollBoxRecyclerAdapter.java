package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;

import java.util.ArrayList;
import java.util.List;

public class ScrollBoxRecyclerAdapter extends RecyclerFragmentAdapter {

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CONTENT = 0;
    private static final int CORNER_RADIUS = 200;

    private Context mContext;
    private List<LineItem> mLineItems;
    private RecyclerFragment mFragment;

    public ScrollBoxRecyclerAdapter(RecyclerFragment fragment) {
        super(fragment, new ArrayList<LineItem>());
        mContext = fragment.getActivity();
        mFragment = fragment;
        mLineItems = new ArrayList<>();
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
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == VIEW_TYPE_HEADER) {
            view = inflater.inflate(R.layout.header_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.horizontal_scroll, parent, false);
        }
        return new ScrollBoxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder viewHolder, int position) {
        final LineItem item = mLineItems.get(position);

        ScrollBoxViewHolder scrollBoxViewHolder = (ScrollBoxViewHolder) viewHolder;
        scrollBoxViewHolder.textViewTitle.setText(item.getTitle());

        // Setup horizontal scroll box
        scrollBoxViewHolder.horizontalRecyclerView.setAdapter(
                new HorizontalRecyclerAdapter(mFragment, position));
        scrollBoxViewHolder.horizontalRecyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    }

    static class ScrollBoxViewHolder extends MainViewHolder {
        RecyclerView horizontalRecyclerView;

        public ScrollBoxViewHolder(View view) {
            super(view);
            horizontalRecyclerView = (RecyclerView) view.findViewById(R.id.horizontal_recyclerview);
        }
    }
}
