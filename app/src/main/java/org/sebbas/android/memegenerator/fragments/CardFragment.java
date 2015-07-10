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
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.activities.EditorActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public class CardFragment extends BaseFragment implements ToolbarCallback {

    public static final String TAG = "CardFragment";
    private static final String ARG_LINE_ITEM = "lineItem";
    private static final String ARG_POSITION_IN_PARENT = "ARG_POSITION_IN_PARENT";

    private LineItem mLineItem;
    private int mPositionInParent;

    public static CardFragment newInstance(LineItem lineItem, int position) {
        CardFragment cardFragment = new CardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LINE_ITEM, lineItem);
        args.putInt(ARG_POSITION_IN_PARENT, position);
        cardFragment.setArguments(args);
        return cardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mLineItem = args.getParcelable(ARG_LINE_ITEM);
            mPositionInParent = args.getInt(ARG_POSITION_IN_PARENT, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.card_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);

        // Maybe use this for smaller images ? -> But problem: gif then wont load ...
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

    @Override
    public boolean isVisibleToUser() {
        if (getActivity() != null) {
            return ((BaseActivity) getActivity()).getMainPagerPosition() == mPositionInParent;
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onToolbarBackPressed() {

    }
}
