package org.sebbas.android.memegenerator.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.fragments.EditorFragment;
import org.sebbas.android.memegenerator.fragments.GalleryFragment;
import org.sebbas.android.memegenerator.fragments.GifFragment;
import org.sebbas.android.memegenerator.fragments.SimplePreferenceFragment;
import org.sebbas.android.memegenerator.fragments.MemeFragment;

public class MainActivityAdapter extends SlidingTabsAdapter {

    private static final String TAG = "MainActivityAdapter";

    private static final int TAB_ICONS[] = {
            R.drawable.selector_meme_icon,
            R.drawable.selector_gif_icon,
            R.drawable.selector_editor_icon,
            R.drawable.selector_gallery_icon,
            R.drawable.selector_preferences_icon};

    private Context mContext;

    public MainActivityAdapter(Context context, FragmentManager fragmentManager, int[] titleResources) {
        super(context, fragmentManager, titleResources);
        mContext = context;
    }

    @Override
    protected Fragment createItem(int position) {
        switch (position) {
            case 0:
                return MemeFragment.newInstance();
            case 1:
                return GifFragment.newInstance();
            case 2:
                return EditorFragment.newInstance();
            case 3:
                return GalleryFragment.newInstance();
            case 4:
                return SimplePreferenceFragment.newInstance();
            default:
                return MemeFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = mContext.getResources().getDrawable(TAB_ICONS[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
