package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.sebbas.android.memegenerator.R;

public class EditorFragment extends SimpleFragment {

    private static final String TAG = "EditorFragment";

    private Picasso mPicasso;
    private ImageView mImageView;
    private String mImageUrl;
    private String mImageId;
    private int mImageWidth;
    private int mImageHeight;

    public static EditorFragment newInstance() {
        return new EditorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Picasso object with okhttp
        OkHttpClient okHttpClient = new OkHttpClient();
        mPicasso = new Picasso.Builder(this.getActivity())
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        mImageView = (ImageView) view.findViewById(R.id.item_image);

        //setupFragmentToolbarAt(0);
        //registerFragmentToolbarCallbacks(0);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*Ion.with(mImageView)
                .error(android.R.color.holo_red_dark)
                .resize(mImageWidth, mImageHeight)
                .load(Utils.imageUrlToThumbnailUrl(mImageUrl, mImageId, UIOptions.THUMBNAIL_SIZE_EDITOR))
                .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        Ion.with(mImageView)
                                .error(mImageView.getDrawable())
                                .crossfade(true)
                                .load(mImageUrl);
                    }
                });*/
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    /*@Override
    void setupFragmentToolbarAt(int position) {
        int titleResource = R.string.editor;
        int menuResource = R.menu.menu_simple_fragment;

        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.setupToolbar(titleResource, menuResource, true);
    }

    @Override
    void registerFragmentToolbarCallbacks(int position) {
        ((BaseActivity) getActivity()).registerToolbarCallback(this);
    }*/
}
