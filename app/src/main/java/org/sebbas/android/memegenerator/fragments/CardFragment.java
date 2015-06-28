package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;

public class CardFragment extends BaseFragment {

    public static final String TAG = "CardFragment";
    private static final String ARG_LINE_ITEM = "lineItem";

    private LineItem mLineItem;

    public static CardFragment newInstance(LineItem lineItem) {
        CardFragment cardFragment = new CardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LINE_ITEM, lineItem);
        cardFragment.setArguments(args);
        return cardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLineItem = getArguments().getParcelable(ARG_LINE_ITEM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.card_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image_card);

        // Maybe use this for smaller images ?
        String formattedImageUrl = Utils.imageUrlToThumbnailUrl(mLineItem.getImageUrl(),
                mLineItem.getImageId(), Utils.IMAGE_MEDIUM);

        Glide.with(getActivity())
                .load(mLineItem.getImageUrl())
                .override(mLineItem.getImageWidth(), mLineItem.getImageHeight())
                .centerCrop()
                .into(imageView);

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
