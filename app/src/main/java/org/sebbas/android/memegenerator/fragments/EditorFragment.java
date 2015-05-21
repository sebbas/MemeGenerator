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
import org.sebbas.android.memegenerator.activities.BaseActivity;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public class EditorFragment extends SimpleFragment implements ToolbarCallback{

    private Picasso mPicasso;
    private ImageView mImageView;
    private String mImageUrl;

    public static EditorFragment newInstance(String imageUrl) {
        EditorFragment editorFragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        editorFragment.setArguments(args);
        return editorFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Picasso object with okhttp
        OkHttpClient okHttpClient = new OkHttpClient();
        mPicasso = new Picasso.Builder(this.getActivity())
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();

        // Get image url
        mImageUrl = getArguments().getString("imageUrl");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        mImageView = (ImageView) view.findViewById(R.id.item_image);

        setupFragmentToolbarAt(0);
        registerFragmentToolbarCallbacks(0);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPicasso.with(this.getActivity())
                .load(mImageUrl)
                .placeholder(android.R.color.white)
                .error(android.R.color.white)
                .tag(this.getActivity())
                .fit()
                .centerInside()
                .into(mImageView);
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
    public void onRefreshClicked() {

    }

    @Override
    public void onBackPressed() {
        getActivity().onBackPressed();
    }

    @Override
    void setupFragmentToolbarAt(int position) {
        int titleResource = R.string.editor;
        int menuResource = R.menu.menu_simple_fragment;

        BaseActivity parentActivity = (BaseActivity) getActivity();
        parentActivity.setupToolbar(parentActivity, titleResource, menuResource, true);
    }

    @Override
    void registerFragmentToolbarCallbacks(int position) {
        ((BaseActivity) getActivity()).registerToolbarCallback(this);
    }
}
