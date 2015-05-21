package org.sebbas.android.memegenerator.interfaces;

public interface DataLoaderCallback {

    public void onDataLoadSuccessful();
    public void onConnectionUnavailable();
    public void onConnectionTimeout();

}