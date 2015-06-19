package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.sebbas.android.memegenerator.CardPagerAdapter;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;

import java.util.ArrayList;

public class EditorFragment extends BaseFragment {

    private static final String TAG = "EditorFragment";

    private Picasso mPicasso;
    private int mStartPosition;
    private ViewPager mCardPager;
    private ArrayList<LineItem> mLineItems;

    public static EditorFragment newInstance(int clickPosition, ArrayList<LineItem> lineItems) {
        EditorFragment editorFragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putInt("startPosition", clickPosition);
        args.putParcelableArrayList("lineItems", lineItems);
        editorFragment.setArguments(args);
        return editorFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStartPosition = getArguments().getInt("startPosition");
        mLineItems = getArguments().getParcelableArrayList("lineItems");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        mCardPager = (ViewPager) view.findViewById(R.id.card_pager);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardPagerAdapter cardPagerAdapter = new CardPagerAdapter(this.getActivity(), getImageUrls());
        mCardPager.setAdapter(cardPagerAdapter);
        mCardPager.setCurrentItem(mStartPosition);
        mCardPager.setPageTransformer(true, new DepthPageTransformer());
        mCardPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // Get title of current item
                LineItem item = mLineItems.get(position);
                String title = item.getTitle();

                // Callback to setup current title in toolbar
                EditorFragment.super.onFragmentChangeToolbar(title);
            }
        });
    }

    private ArrayList<String> getImageUrls() {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (LineItem lineItem : mLineItems) {
            String imageUrl = lineItem.getImageUrl();
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    private class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
