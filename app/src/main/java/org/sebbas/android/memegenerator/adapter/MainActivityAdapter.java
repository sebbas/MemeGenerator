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
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = MemeFragment.newInstance(position);
                break;
            case 1:
                fragment = GifFragment.newInstance(position);
                break;
            case 2:
                fragment = EditorFragment.newInstance(position);
                break;
            case 3:
                fragment = GalleryFragment.newInstance(position);
                break;
            case 4:
                fragment = SimplePreferenceFragment.newInstance(position);
                break;
            default:
                fragment = MemeFragment.newInstance(position);
        }
        return fragment;
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
