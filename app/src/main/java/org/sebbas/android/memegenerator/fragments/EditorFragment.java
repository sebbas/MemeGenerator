package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.sebbas.android.memegenerator.CardPagerAdapter;
import org.sebbas.android.memegenerator.R;

import java.util.ArrayList;

public class EditorFragment extends BaseFragment {

    private static final String TAG = "EditorFragment";

    private Picasso mPicasso;
    private String mImageUrl;
    private ArrayList<String> mImageUrls;
    private ArrayList<Integer> mImageWidths;
    private ArrayList<Integer> mImageHeights;
    private int mStartPosition;
    private ViewPager mCardPager;

    private String mImageId;
    private int mImageWidth;
    private int mImageHeight;

    public static EditorFragment newInstance(int position, ArrayList<String> imageUrls,
                                             ArrayList<Integer> imageWidths,ArrayList<Integer> imageHeights) {
        EditorFragment editorFragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putStringArrayList("imageUrls", imageUrls);
        args.putIntegerArrayList("imageWidths", imageWidths);
        args.putIntegerArrayList("imageHeights", imageHeights);
        editorFragment.setArguments(args);
        return editorFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStartPosition = getArguments().getInt("position");
        mImageUrls = getArguments().getStringArrayList("imageUrls");
        mImageWidths = getArguments().getIntegerArrayList("imageWidths");
        mImageHeights = getArguments().getIntegerArrayList("imageHeights");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        mCardPager = (ViewPager) view.findViewById(R.id.card_pager);
        /*mCardPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                int viewPagerWidth = mCardPager.getWidth();
                float imageRatio = (float) mImageWidths.get(position) / (float) mImageHeights.get(position);
                float viewPagerHeight = (float) (viewPagerWidth * imageRatio);

                layoutParams.width = viewPagerWidth;
                layoutParams.height = (int) viewPagerHeight;

                mCardPager.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardPagerAdapter cardPagerAdapter = new CardPagerAdapter(this.getActivity(), mImageUrls, mImageHeights);
        mCardPager.setAdapter(cardPagerAdapter);
        mCardPager.setCurrentItem(mStartPosition);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
