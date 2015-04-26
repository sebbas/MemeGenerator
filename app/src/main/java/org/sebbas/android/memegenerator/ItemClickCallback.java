package org.sebbas.android.memegenerator;

import java.util.ArrayList;

public interface ItemClickCallback {
    public void onItemClick(int itemPosition, ArrayList<SimpleRecyclerAdapter.LineItem> lineItems);
}
