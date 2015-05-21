package org.sebbas.android.memegenerator.interfaces;

public interface ToolbarCallback {

    boolean onQueryTextSubmit(String s);
    boolean onQueryTextChange(String s);
    void onRefreshClicked();
    void onBackPressed();
}
