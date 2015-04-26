package org.sebbas.android.memegenerator;

public interface DataLoaderCallback {

    public void onDataLoadSuccessful();
    public void onConnectionUnavailable();
    public void onConnectionTimeout();
    public void onDataLoadItemSuccessful(int position);

}