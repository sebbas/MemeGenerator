package org.sebbas.android.memegenerator.interfaces;

import android.graphics.Bitmap;

import org.sebbas.android.memegenerator.LineItem;

import java.util.List;

public interface ItemClickCallback {

    public void onItemClick(int itemPosition, List<LineItem> lineItems);
}
