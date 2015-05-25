package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;
import com.squareup.picasso.Transformation;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.PicassoCache;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.dataloader.JsonDataLoader;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;

import java.util.ArrayList;
import java.util.List;

public class CardsRecyclerAdapter extends RecyclerFragmentAdapter {

    private static final int VIEW_TYPE_FILLER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private Context mContext;
    private List<LineItem> mLineItems;
    private JsonDataLoader mJsonDataLoader;
    private int mPageIndex = 0;
    private RecyclerFragment mFragment;
    private View mFillerView;

    public CardsRecyclerAdapter(RecyclerFragment fragment, View fillerView) {
        mContext = fragment.getActivity();
        mFragment = fragment;
        mLineItems = new ArrayList<>();
        mFillerView = fillerView;

        int fragmentType = fragment.getFragmentType();

        mJsonDataLoader = new JsonDataLoader(fragment, fragmentType);
        refreshData();
    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FILLER : VIEW_TYPE_CONTENT;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardViewHolder.ViewHolderCallback viewHolderCallback = new CardViewHolder.ViewHolderCallback() {
            @Override
            public void onItemClick(int position) {
                // Only trigger click event for content items
                if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
                    ((ItemClickCallback) mContext).onItemClick(position, mLineItems);
                }
            }
        };

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case VIEW_TYPE_FILLER:
                view = inflater.inflate(R.layout.recycler_header_big, parent, false);
                break;
            default:
                view = inflater.inflate(R.layout.card_item, parent, false);
        }
        return new CardViewHolder(view, viewHolderCallback);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder viewHolder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            final LineItem item = mLineItems.get(position);

            final CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;
            cardViewHolder.textViewTitle.setText(item.getTitle());

            Transform transformation = new Transform() {

                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = cardViewHolder.imageView.getWidth();

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);

                    /*int[] maxTextureSize = new int[1];
                    gl.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
                    int maxTexture = maxTextureSize[0];*/

                if (targetHeight > Utils.DEFAULT_MAX_BITMAP_DIMENSION) {
                        targetHeight = Utils.DEFAULT_MAX_BITMAP_DIMENSION;
                        targetWidth = (int) (targetHeight / aspectRatio);
                    }
                    if (targetWidth > Utils.DEFAULT_MAX_BITMAP_DIMENSION) {
                        targetWidth = Utils.DEFAULT_MAX_BITMAP_DIMENSION;
                        targetHeight = (int) (targetWidth * aspectRatio);
                    }

                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "transformation" + " desiredWidth";
                }
            };

            Ion.with(cardViewHolder.imageView)
                    .placeholder(android.R.color.holo_blue_bright)
                    .error(android.R.color.holo_red_dark)
                            //.animateLoad(spinAnimation)
                            //.animateIn(fadeInAnimation)
                    //.resize(0, Utils.DEFAULT_MAX_BITMAP_DIMENSION)
                    //.transform(transformation)
                    .load(item.getImageUrl());//(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_LARGE));

            /*PicassoCache.getPicassoInstance(mContext)
                    .load(item.getImageUrl())//(Utils.imageUrlToThumbnailUrl(item.getImageUrl(), item.getImageId(), Utils.IMAGE_LARGE))
                    .placeholder(android.R.color.holo_blue_bright)
                    .error(android.R.color.holo_red_dark)
                    .tag(mContext)
                    .resize(0, Utils.DEFAULT_MAX_BITMAP_DIMENSION)
                    .transform(transformation)
                    .into(cardViewHolder.imageView);*/

            cardViewHolder.textViewViewCount.setText(Utils.getViewCountString(mContext, item.getViewCount()));
            cardViewHolder.textViewTimeStamp.setText(Utils.getTimeAgoString(mContext, Integer.valueOf(item.getTimeStamp())));
        }
    }

    static class CardViewHolder extends MainViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textViewViewCount;
        TextView textViewTimeStamp;

        public CardViewHolder(View view, ViewHolderCallback viewHolderCallback) {
            super(view, viewHolderCallback);
            view.setOnClickListener(this);

            imageView = (ImageView) view.findViewById(R.id.item_image);
            textViewViewCount = (TextView) view.findViewById(R.id.item_viewcount);
            textViewTimeStamp = (TextView) view.findViewById(R.id.item_datetime);
        }

        @Override
        public void onClick(View v) {
            //GifDrawable gifDrawable = (GifDrawable) imageView.getDrawable();
            //gifDrawable.start();
            mViewHolderCallback.onItemClick(getPosition());
        }
    }

    @Override
    protected List<LineItem> getLineItems() {
        return mJsonDataLoader.getLineItems(null, false);
    }

    @Override
    public void refreshUI() {
        mLineItems = getLineItems();
        notifyDataSetChanged();
    }

    @Override
    public void refreshData() {
        String url = Utils.getUrlForData(mPageIndex, mFragment);
        mJsonDataLoader.load(url);
    }
}
