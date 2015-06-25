package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.sebbas.android.memegenerator.adapter.CardPagerAdapter;
import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

import java.util.ArrayList;

public class EditorFragment extends BaseFragment implements ToolbarCallback {

    public static final String TAG = "EditorFragment";
    private static final String START_POSITION = "startPosition";
    private static final String LINE_ITEMS = "lineItems";

    private Picasso mPicasso;
    private int mStartPosition;
    private ViewPager mCardPager;
    private ArrayList<LineItem> mLineItems;

    public static EditorFragment newInstance(int clickPosition, ArrayList<LineItem> lineItems) {
        EditorFragment editorFragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putInt(START_POSITION, clickPosition);
        args.putParcelableArrayList(LINE_ITEMS, lineItems);
        editorFragment.setArguments(args);
        return editorFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStartPosition = getArguments().getInt(START_POSITION);
        mLineItems = getArguments().getParcelableArrayList(LINE_ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        mCardPager = (ViewPager) view.findViewById(R.id.card_pager);
        setToolbarTitle(mStartPosition);
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
                setToolbarTitle(position);

            }
        });
    }

    private void setToolbarTitle(int position) {
        // Get title of current item
        LineItem item = mLineItems.get(position);
        String title = item.getTitle();

        // Callback to setup current title in toolbar
        super.onFragmentChangeToolbar(TAG, title);
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onBackPressed() {
        getActivity().onBackPressed();
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
