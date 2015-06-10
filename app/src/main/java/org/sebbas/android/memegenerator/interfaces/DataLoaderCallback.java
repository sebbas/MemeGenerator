package org.sebbas.android.memegenerator.interfaces;

public interface DataLoaderCallback {

    void onDataLoadSuccessful();
    void onConnectionUnavailable();
    void onConnectionTimeout();
    void onFilterComplete();

}