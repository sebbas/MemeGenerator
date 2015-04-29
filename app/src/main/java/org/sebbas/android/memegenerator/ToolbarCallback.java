package org.sebbas.android.memegenerator;

public interface ToolbarCallback {

    public boolean onQueryTextSubmit(String s);
    public boolean onQueryTextChange(String s);
    public void onRefreshClicked();
}
