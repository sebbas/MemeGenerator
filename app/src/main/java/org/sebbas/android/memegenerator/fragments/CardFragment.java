package org.sebbas.android.memegenerator.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.Utils;
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public class CardFragment extends BaseFragment implements ToolbarCallback {

    public static final String TAG = "CardFragment";
    private static final String ARG_LINE_ITEM = "lineItem";
    private static final String ARG_POSITION_IN_PARENT = "ARG_POSITION_IN_PARENT";

    private LineItem mLineItem;
    private int mPositionInParent;
    private RelativeLayout mContainerTextOne;

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
        mContainerTextOne = (RelativeLayout) view.findViewById(R.id.container_text_one);
        TextView textOne = (TextView) view.findViewById(R.id.text_one);

        // Maybe use this for smaller images ? -> But problem: gif then wont load ...
        String formattedImageUrl = Utils.imageUrlToThumbnailUrl(mLineItem.getImageUrl(),
                mLineItem.getImageId(), Utils.IMAGE_MEDIUM);

        // Setup image
        Glide.with(getActivity())
                .load(mLineItem.getImageUrl())
                .override(mLineItem.getImageWidth(), mLineItem.getImageHeight())
                .centerCrop()
                .into(imageView);

        // Setup texts
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 150);
        textOne.setLayoutParams(layoutParams);
        textOne.setOnTouchListener(new TextDragListener());
        setTypeFace(textOne);

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

    private class TextDragListener implements View.OnTouchListener {

        private int deltaX;
        private int deltaY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    deltaX = X - lParams.leftMargin;
                    deltaY = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - deltaX;
                    layoutParams.topMargin = Y - deltaY;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    break;
            }
            mContainerTextOne.invalidate();
            return true;
        }
    }

    private void setTypeFace(TextView textView) {
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/impact.ttf");
        textView.setTypeface(typeFace);
    }
}
