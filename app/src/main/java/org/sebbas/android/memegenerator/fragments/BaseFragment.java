package org.sebbas.android.memegenerator.fragments;

import android.app.Activity;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.util.TypedValue;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.interfaces.FragmentCallback;

public abstract class BaseFragment extends Fragment {

    private FragmentCallback mFragmentCallback;

    protected int getActionBarSize() {
        Activity activity = getActivity();
        if (activity == null) {
            return 0;
        }
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = activity.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected int getScreenHeight() {
        Activity activity = getActivity();
        if (activity == null) {
            return 0;
        }
        return activity.findViewById(android.R.id.content).getHeight();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentCallback = (FragmentCallback) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + FragmentCallback.class.toString());
        }
    }

    protected void onFragmentComplete(BaseFragment baseFragment) {
        mFragmentCallback.onFragmentComplete(baseFragment);
    }

    protected void onFragmentChangeToolbar(String fragmentTag, String toolbarTitle) {
        mFragmentCallback.onFragmentChangeToolbar(fragmentTag, toolbarTitle);
    }

    abstract public String getFragmentTag();
}
