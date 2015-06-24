package org.sebbas.android.memegenerator.interfaces;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.fragments.BaseFragment;

import java.util.ArrayList;


public interface FragmentCallback {

    void onFragmentComplete(BaseFragment baseFragment);
    void onFragmentChangeToolbar(String fragmentTag, String toolbarTitle);
    void onItemClick(int itemPosition, ArrayList<LineItem> lineItems);
}
