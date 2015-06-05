package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CardsRecyclerAdapter extends RecyclerFragmentAdapter {

    private static final int VIEW_TYPE_FILLER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private Context mContext;
    private List<LineItem> mLineItems;
    private int mPageIndex = 0;
    private RecyclerFragment mFragment;
    private BitmapRegionDecoder mDecoder;

    public CardsRecyclerAdapter(RecyclerFragment fragment) {
        mContext = fragment.getActivity();
        mFragment = fragment;
        mLineItems = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mLineItems.size();
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

        if (viewType == VIEW_TYPE_FILLER) {
            view = inflater.inflate(R.layout.recycler_padding, parent, false);
        } else {
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

    /**
     * Creates BitmapRegionDecoder from an image in assets.
     */
    private BitmapRegionDecoder createDecoder(String imageUrl){
        InputStream is = null;
        try {
            is = new URL(imageUrl).openStream();
            return BitmapRegionDecoder.newInstance(new BufferedInputStream(is), true);
        } catch (IOException e) {
            throw new RuntimeException("Could not create BitmapRegionDecoder", e);
        }
    }

    /*private Bitmap getPreview(String imageUrl) {
        Bitmap bitmap = null;
        BitmapRegionDecoder bitmapRegionDecoder = createDecoder(imageUrl);
        bitmap = bitmapRegionDecoder.decodeRegion(getRectForIndex(i, mDecoder.getWidth()), null);
        return bitmap;
    }

    private Rect getRectForIndex() {
        // the resulting rectangle
        return new Rect(left, top, right, bottom);
    }*/
}
